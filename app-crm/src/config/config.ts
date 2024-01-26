interface Config {
    API_URL: string;
    // ... other configuration properties
}

const config: Config = {
    API_URL: import.meta.env.VITE_APP_API_URL
    // ... other configuration values
};

export default config;
