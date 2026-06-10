import axiosInstance from './axiosInstance';

export const chatApi = {
    send: (data) => axiosInstance.post('/api/chat', data),
};
