#!/bin/bash
CONTAINER_NAME="sabores-conectados-broker"
KAFKA_BROKER="localhost:9092"
TOPICS=("order-topic" "reservation-topic")
PARTITIONS=3
REPLICATION_FACTOR=1
MAX_ATTEMPTS=20
WAIT_TIME=5
KAFKA_CLI="/opt/kafka/bin/kafka-topics.sh"

# --- Pre-check: Ensure the container is running ---
if ! docker inspect -f '{{.State.Running}}' "$CONTAINER_NAME" 2>/dev/null | grep -q "true"; then
    echo "❌ Error: Container '$CONTAINER_NAME' is not running. Please start your services first." >&2
    exit 1
fi

echo "--- Starting Kafka Topic Initialization ---"
echo "Topics to create: ${TOPICS[@]}"
echo "Step 1: Entering container '$CONTAINER_NAME' and executing script..."

# --- Execute script inside the Docker container ---
docker exec -i "$CONTAINER_NAME" /bin/bash << EOF
    # Set the variables again inside the container's environment
    KAFKA_BROKER="$KAFKA_BROKER"
    KAFKA_CLI="$KAFKA_CLI"
    PARTITIONS="$PARTITIONS"
    REPLICATION_FACTOR="$REPLICATION_FACTOR"
    MAX_ATTEMPTS="$MAX_ATTEMPTS"
    WAIT_TIME="$WAIT_TIME"

    # Convert the external array into a space-separated string for internal use
    TOPICS_LIST="${TOPICS[*]}"

    # --- Function Definitions (Executed inside the container) ---

    wait_for_kafka() {
        echo "Waiting for Kafka broker (\$KAFKA_BROKER) to be ready..."

        for i in \$(seq 1 \$MAX_ATTEMPTS); do
            # Use the absolute path for the CLI tool
            "\$KAFKA_CLI" --bootstrap-server "\$KAFKA_BROKER" --list 2>/dev/null
            if [ \$? -eq 0 ]; then
                echo "✅ Kafka broker is ready."
                return 0
            else
                echo "Attempt \$i/\$MAX_ATTEMPTS: Kafka not ready, waiting \$WAIT_TIME seconds..."
                sleep "\$WAIT_TIME"
            fi
        done

        echo "❌ Kafka broker failed to start within the expected time." >&2
        return 1
    }

    # Modified to accept the topic name as an argument
    create_topic() {
        local TOPIC_NAME="\$1"
        echo "Creating topic \$TOPIC_NAME..."

        "\$KAFKA_CLI" \
            --create \
            --topic "\$TOPIC_NAME" \
            --bootstrap-server "\$KAFKA_BROKER" \
            --partitions "\$PARTITIONS" \
            --replication-factor "\$REPLICATION_FACTOR" \
            --if-not-exists

        if [ \$? -eq 0 ]; then
            echo "✅ Successfully created or ensured topic '\$TOPIC_NAME'"
            return 0
        else
            echo "❌ Failed to create topic '\$TOPIC_NAME'." >&2
            # Return failure status
            return 1
        fi
    }

    # --- Main Execution inside the container ---
    wait_for_kafka || exit 1

    ALL_SUCCESS=0

    # Loop through the list of topics
    for topic in \$TOPICS_LIST; do
        create_topic "\$topic"
        if [ \$? -ne 0 ]; then
            ALL_SUCCESS=1
            echo "Stopping execution due to failure to create topic: \$topic" >&2
            break
        fi
    done

    # Exit with the combined success status
    exit \$ALL_SUCCESS
EOF

# --- Post-check: Script execution status outside the container ---
if [ $? -eq 0 ]; then
    echo "--- Initialization Successful. Topics '${TOPICS[*]}' are now available. ---"
else
    echo "--- Initialization Failed. Review logs above. ---" >&2
    exit 1
fi