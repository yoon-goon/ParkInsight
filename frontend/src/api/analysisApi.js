import axiosInstance from './axiosInstance';

export const analysisApi = {
    analyze: (formData) => axiosInstance.post('/api/analysis', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
        timeout: 120000,
    }),
    getAnalysis: (id) => axiosInstance.get(`/api/analysis/${id}`),
    getHistory: () => axiosInstance.get('/api/analysis/history'),
};
