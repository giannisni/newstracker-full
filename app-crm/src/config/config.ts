import { Index } from "react-instantsearch";

interface Config {
    API_URL: string;
    INDEX_NAME:string
    // ... other configuration properties
}

const config: Config = {
    API_URL: import.meta.env.VITE_APP_API_URL,
    INDEX_NAME : import.meta.env.VITE_APP_API_INDEX
    // ... other configuration values
};

export default config;
