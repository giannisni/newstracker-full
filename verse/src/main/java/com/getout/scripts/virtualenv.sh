#!/bin/bash
# Script to setup Python virtual environment

VENV_PATH="/path/to/your/venv"

# Check if the virtual environment already exists
if [ ! -d "$VENV_PATH" ]; then
    echo "Creating virtual environment..."
    python -m venv $VENV_PATH
else
    echo "Virtual environment already exists."
fi

# Activate the virtual environment
source $VENV_PATH/bin/activate

# Install requirements
pip install -r requirements.txt

# Deactivate virtual environment
deactivate
