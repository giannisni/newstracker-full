import React, { useEffect } from "react";
import { useSearchParams } from "react-router-dom";

import { AuthPage } from "@refinedev/antd";
import { useLogin } from "@refinedev/core";

import { GithubOutlined, GoogleOutlined } from "@ant-design/icons";

import { Title } from "@/components";
import { demoCredentials } from "@/providers";

export const LoginPage: React.FC = () => {
    const [searchParams] = useSearchParams();
    const { mutate } = useLogin();

    const emailFromSearchParams = searchParams.get("email");
    const accessToken = searchParams.get("accessToken");
    const refreshToken = searchParams.get("refreshToken");

    const initialValues = emailFromSearchParams
        ? { email: emailFromSearchParams }
        : demoCredentials;

    useEffect(() => {
        if (accessToken && refreshToken) {
            mutate({
                accessToken,
                refreshToken,
            });
        }
    }, [accessToken, refreshToken]);
    // const handleGuestLogin = () => {
    //     // Example: Set guest user state in your global state/context
    //     setAuthState({
    //       isAuthenticated: true,
    //       isGuest: true,
    //       user: null, // No user data for guests
    //       token: 'guest-access-token' // A predefined or dynamically generated token for guest access
    //     });
      
    //     // Navigate to the main area of the application or a specific page for guests
    //     navigate('/dashboard'); // Using React Router's `useNavigate` hook
    //   };

    return (
        <AuthPage
            type="login"
            formProps={{
                initialValues,
            }}
            contentProps={{
                className: "auth-page",
            }}
            title={<Title collapsed={false} />}
            // providers={[
            //     {
            //         name: "google",
            //         label: "Sign in with Google",
            //         icon: (
            //             <GoogleOutlined
            //                 style={{
            //                     fontSize: 24,
            //                     lineHeight: 0,
            //                 }}
            //             />
            //         ),
            //     },
            //     {
            //         name: "github",
            //         label: "Sign in with GitHub",
            //         icon: (
            //             <GithubOutlined
            //                 style={{
            //                     fontSize: 24,
            //                     lineHeight: 0,
            //                 }}
            //             />
            //         ),
            //     },
                
            // ]}
            // children={
            //     <div style={{ textAlign: 'center', marginTop: 20 }}>
            //         <button onClick={handleGuestLogin} style={{ padding: '10px 20px', cursor: 'pointer' }}>
            //             Continue as Guest
            //         </button>
            //     </div>
            // }
            
        />
        
    );
};
