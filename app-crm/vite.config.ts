import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import tsconfigPaths from "vite-tsconfig-paths";

// https://vitejs.dev/config/
export default defineConfig({
    plugins: [tsconfigPaths(), react()],
    server: {
        // Conditionally configure the server based on the environment
          cors: {
            // Development-specific CORS configuration
            origin: 'http://localhost:5173',
            credentials: true,
          }
        },
        // Additional development server configurations can go here
   
    build: {
        rollupOptions: {
            output: {
                manualChunks: {
                    antd: ["antd"],
                },
            },
        },
    },
});
