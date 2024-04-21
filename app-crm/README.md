# Refine Dashboard App - Front End

This document outlines the structure and components of the front end for the Refine Dashboard app, providing insights into the various charts and metrics displayed.

## Main Files

### Index First Page

- **Location**: `app-crm/src/routes/dashboard/index.tsx`
- **Description**: This is the entry point for the dashboard, where the overall layout and integration of components are defined.

## Dashboard Components

### Topics Pie Chart

- **File**: `app-crm/src/routes/dashboard/components/tasks-chart.tsx`
- **Description**: Displays a pie chart representing the distribution of various topics.

### Sentiment Analysis Chart

- **File**: `app-crm/src/routes/dashboard/components/bias-chart.tsx`
- **Description**: A chart that analyzes and visualizes sentiment trends within the data.

### Keyword Count Chart

- **File**: `app-crm/src/routes/dashboard/components/deals-chart.tsx`
- **Description**: Visualizes the count of specific keywords across different datasets or timeframes.

### Highlights Chart

- **File**: `app-crm/src/routes/dashboard/components/highlights.tsx`
- **Description**: Summarizes key insights or highlights from the data analysis.

### Terms Percentage Chart Related to "Genocide"

- **File**: `app-crm/src/routes/dashboard/components/percentage-chart.tsx`
- **Description**: Shows the percentage of terms used in relation to the term "genocide".

### Terms Percentage in Relation to Another

- **File**: `app-crm\src\routes\dashboard\components\percentage-keyword.tsx`
- **Description**: Visualizes the percentage of one term in relation to another selected term.

### Wordcloud Chart

- **File**: `app-crm/src/routes/dashboard/components/word-cloud.tsx`
- **Description**: A word cloud that displays the prominence of words based on their frequency or significance in the dataset.

## Configuration

### API URL and Index Configuration

- **File**: `app-crm/src/config/config.ts`
- **Description**: This file contains the configuration settings for the API URL and other global settings that affect the entire application.

## Future Work

### Authentication

- **Login and Register**: Implement user authentication workflows for login and registration. This includes setting up secure user accounts and managing sessions.

### React Hooks

- **Hooks Instead of REST Calls**: Transition from traditional REST API calls to using React Hooks for data fetching and state management. This will make the app more efficient and the codebase cleaner.

### Check Refine Documentation

- **Refine.dev Data Provider**: Explore the use of Refine's data provider hooks to streamline data interactions. More details can be found in the Refine documentation:
    - [Refine Data Provider Documentation](https://refine.dev/docs/data/data-provider/)

### Dashboard Redesign

- **Complete Redesign**: Consider a complete overhaul of the dashboard design and functionality based on user feedback and new requirements. This might include changing the layout, adding new features, or enhancing existing ones to improve user experience and data visualization.

# Running the Application

## Start Command

To start the development server and launch the application in a development environment, use the following command:

```bash
npm run start
