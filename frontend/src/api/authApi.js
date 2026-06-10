import axiosInstance from './axiosInstance';

export const authApi = {
    signup: (data) => axiosInstance.post('/api/auth/signup', data),
    login: (data) => axiosInstance.post('/api/auth/login', data),
};
