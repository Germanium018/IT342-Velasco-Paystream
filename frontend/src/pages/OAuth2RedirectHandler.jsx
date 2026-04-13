import React, { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import axios from 'axios';

const OAuth2RedirectHandler = () => {
    const navigate = useNavigate();
    const location = useLocation();

    useEffect(() => {
        const params = new URLSearchParams(location.search);
        const token = params.get('token');

        if (token) {
            localStorage.setItem('token', token);

            // Fetch user profile to get roles and name
            const fetchUserData = async () => {
                try {
                    const response = await axios.get('http://localhost:8080/api/v1/auth/me', {
                        headers: { Authorization: `Bearer ${token}` }
                    });

                    localStorage.setItem('role', response.data.role);
                    localStorage.setItem('user', JSON.stringify(response.data.user));

                    if (response.data.role === 'ROLE_ADMIN') {
                        navigate('/dashboard');
                    } else {
                        navigate('/employee-dashboard');
                    }
                } catch (error) {
                    console.error("Auth sync failed", error);
                    navigate('/login');
                }
            };

            fetchUserData();
        } else {
            navigate('/login');
        }
    }, [location, navigate]);

    return (
        <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', flexDirection: 'column' }}>
            <div className="loader"></div>
            <p style={{ marginTop: '20px', color: '#64748b' }}>Finalizing secure GitHub connection...</p>
        </div>
    );
};

export default OAuth2RedirectHandler;