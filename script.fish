#!/usr/bin/env fish

# Base folder is the current folder (chess folder)
set BASE_DIR (pwd)

# Create log folder next to src
set LOG_DIR "$BASE_DIR/log"
mkdir -p $LOG_DIR

# List of packages inside src/main/java/com/github/ahansantra/chess
set PACKAGES board pieces sql ui utils

# Create package folders inside log
for pkg in $PACKAGES
    mkdir -p "$LOG_DIR/$pkg"
    # Create a basic log file inside each package
    touch "$LOG_DIR/$pkg/$pkg.log"
end

# Optional: create a general log file in log folder
touch "$LOG_DIR/general.log"

echo "Log folders and files created successfully:"
tree -L 2 $LOG_DIR
