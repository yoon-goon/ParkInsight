import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import {
    Box, Button, Card, CardContent, CardActions, Dialog, DialogTitle,
    DialogContent, DialogActions, TextField, Typography, Grid, Chip,
    IconButton, CircularProgress, Alert,
} from '@mui/material';
import DeleteIcon from '@mui/icons-material/Delete';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import AddIcon from '@mui/icons-material/Add';
import NavBar from '../components/NavBar';
import { vehicleApi } from '../api/vehicleApi';

const INITIAL_FORM = { model: '', year: new Date().getFullYear(), color: '', mileage: '' };

export default function Vehicles() {
    const navigate = useNavigate();
    const queryClient = useQueryClient();
    const [open, setOpen] = useState(false);
    const [form, setForm] = useState(INITIAL_FORM);

    const { data, isLoading, error } = useQuery({
        queryKey: ['vehicles'],
        queryFn: () => vehicleApi.getVehicles(),
        select: (res) => res.data,
    });

    const createMutation = useMutation({
        mutationFn: vehicleApi.createVehicle,
        onSuccess: () => {
            queryClient.invalidateQueries(['vehicles']);
            setOpen(false);
            setForm(INITIAL_FORM);
        },
    });

    const deleteMutation = useMutation({
        mutationFn: vehicleApi.deleteVehicle,
        onSuccess: () => queryClient.invalidateQueries(['vehicles']),
    });

    const handleChange = (e) => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

    const handleSubmit = () => {
        createMutation.mutate({
            model: form.model,
            year: Number(form.year),
            color: form.color,
            mileage: Number(form.mileage),
        });
    };

    return (
        <Box>
            <NavBar />
            <Box sx={{ p: 3, maxWidth: 1100, mx: 'auto' }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                    <Typography variant="h5" fontWeight={700}>내 차량</Typography>
                    <Button variant="contained" startIcon={<AddIcon />} onClick={() => setOpen(true)}>
                        차량 추가
                    </Button>
                </Box>

                {error && <Alert severity="error" sx={{ mb: 2 }}>차량 목록을 불러오지 못했습니다.</Alert>}

                {isLoading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
                        <CircularProgress />
                    </Box>
                ) : data?.length === 0 ? (
                    <Box sx={{ textAlign: 'center', mt: 8 }}>
                        <DirectionsCarIcon sx={{ fontSize: 64, color: 'text.secondary', mb: 2 }} />
                        <Typography variant="h6" color="text.secondary">등록된 차량이 없습니다.</Typography>
                        <Typography variant="body2" color="text.secondary" mt={1}>
                            차량을 추가하고 AI 분석을 시작해보세요.
                        </Typography>
                    </Box>
                ) : (
                    <Grid container spacing={2}>
                        {data?.map((vehicle) => (
                            <Grid item xs={12} sm={6} md={4} key={vehicle.id}>
                                <Card>
                                    <CardContent>
                                        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                                            <Typography variant="h6" fontWeight={600}>{vehicle.model}</Typography>
                                            <IconButton
                                                size="small"
                                                onClick={() => deleteMutation.mutate(vehicle.id)}
                                                disabled={deleteMutation.isPending}
                                            >
                                                <DeleteIcon fontSize="small" />
                                            </IconButton>
                                        </Box>
                                        <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap', mt: 1 }}>
                                            <Chip label={`${vehicle.year}년식`} size="small" />
                                            <Chip label={vehicle.color} size="small" variant="outlined" />
                                            <Chip label={`${vehicle.mileage.toLocaleString()}km`} size="small" variant="outlined" />
                                        </Box>
                                    </CardContent>
                                    <CardActions>
                                        <Button
                                            size="small"
                                            variant="contained"
                                            fullWidth
                                            onClick={() => navigate(`/analysis/new?vehicleId=${vehicle.id}`)}
                                        >
                                            AI 분석 시작
                                        </Button>
                                    </CardActions>
                                </Card>
                            </Grid>
                        ))}
                    </Grid>
                )}
            </Box>

            <Dialog open={open} onClose={() => setOpen(false)} maxWidth="xs" fullWidth>
                <DialogTitle>차량 추가</DialogTitle>
                <DialogContent sx={{ display: 'flex', flexDirection: 'column', gap: 2, pt: '16px !important' }}>
                    {createMutation.isError && (
                        <Alert severity="error">{createMutation.error?.response?.data?.message}</Alert>
                    )}
                    <TextField name="model" label="차종 (예: 아반떼 CN7)" value={form.model} onChange={handleChange} fullWidth />
                    <TextField name="year" label="연식" type="number" value={form.year} onChange={handleChange} fullWidth />
                    <TextField name="color" label="색상" value={form.color} onChange={handleChange} fullWidth />
                    <TextField name="mileage" label="주행거리 (km)" type="number" value={form.mileage} onChange={handleChange} fullWidth />
                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpen(false)}>취소</Button>
                    <Button variant="contained" onClick={handleSubmit} disabled={createMutation.isPending}>
                        {createMutation.isPending ? '등록 중...' : '등록'}
                    </Button>
                </DialogActions>
            </Dialog>
        </Box>
    );
}
