import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { authApi } from '../api/authApi';

export function useAuth() {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const isLoggedIn = () => !!localStorage.getItem('token');

    const getUser = () => {
        const name = localStorage.getItem('userName');
        const email = localStorage.getItem('userEmail');
        return name ? { name, email } : null;
    };

    const signup = async (data) => {
        setLoading(true);
        setError(null);
        try {
            const res = await authApi.signup(data);
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('userName', res.data.name);
            localStorage.setItem('userEmail', res.data.email);
            navigate('/vehicles');
        } catch (e) {
            setError(e.response?.data?.message || '회원가입에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const login = async (data) => {
        setLoading(true);
        setError(null);
        try {
            const res = await authApi.login(data);
            localStorage.setItem('token', res.data.token);
            localStorage.setItem('userName', res.data.name);
            localStorage.setItem('userEmail', res.data.email);
            navigate('/vehicles');
        } catch (e) {
            setError(e.response?.data?.message || '로그인에 실패했습니다.');
        } finally {
            setLoading(false);
        }
    };

    const logout = () => {
        localStorage.removeItem('token');
        localStorage.removeItem('userName');
        localStorage.removeItem('userEmail');
        navigate('/login');
    };

    return { signup, login, logout, isLoggedIn, getUser, loading, error };
}
