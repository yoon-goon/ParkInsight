import axiosInstance from './axiosInstance';

export const vehicleApi = {
    getVehicles: () => axiosInstance.get('/api/vehicles'),
    createVehicle: (data) => axiosInstance.post('/api/vehicles', data),
    deleteVehicle: (id) => axiosInstance.delete(`/api/vehicles/${id}`),
};
