import { useState } from 'react';
import { useNavigate, useSearchParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
    Box, Button, Typography, Grid, Alert, CircularProgress, Card, CardContent,
    FormControl, InputLabel, Select, MenuItem,
} from '@mui/material';
import NavBar from '../components/NavBar';
import ImageUploadZone from '../components/ImageUploadZone';
import { vehicleApi } from '../api/vehicleApi';
import { analysisApi } from '../api/analysisApi';

export default function NewAnalysis() {
    const navigate = useNavigate();
    const [searchParams] = useSearchParams();
    const defaultVehicleId = searchParams.get('vehicleId');

    const [vehicleId, setVehicleId] = useState(defaultVehicleId || '');
    const [images, setImages] = useState({ front: null, rear: null, left: null, right: null });
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const { data: vehicles } = useQuery({
        queryKey: ['vehicles'],
        queryFn: () => vehicleApi.getVehicles(),
        select: (res) => res.data,
    });

    const handleImageChange = (key) => (file) => {
        setImages(prev => ({ ...prev, [key]: file }));
    };

    const allUploaded = Object.values(images).every(Boolean);

    const handleSubmit = async () => {
        if (!vehicleId || !allUploaded) return;
        setLoading(true);
        setError(null);
        try {
            const formData = new FormData();
            formData.append('vehicleId', vehicleId);
            formData.append('front', images.front);
            formData.append('rear', images.rear);
            formData.append('left', images.left);
            formData.append('right', images.right);

            const res = await analysisApi.analyze(formData);
            navigate(`/analysis/${res.data.id}`);
        } catch (e) {
            setError(e.response?.data?.message || '분석에 실패했습니다. 잠시 후 다시 시도해주세요.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box>
            <NavBar />
            <Box sx={{ p: 3, maxWidth: 700, mx: 'auto' }}>
                <Typography variant="h5" fontWeight={700} mb={3}>AI 차량 분석</Typography>

                {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                <Card sx={{ mb: 3 }}>
                    <CardContent>
                        <Typography variant="subtitle1" fontWeight={600} mb={2}>차량 선택</Typography>
                        <FormControl fullWidth>
                            <InputLabel>차량</InputLabel>
                            <Select value={vehicleId} label="차량" onChange={(e) => setVehicleId(e.target.value)}>
                                {vehicles?.map((v) => (
                                    <MenuItem key={v.id} value={v.id}>
                                        {v.model} ({v.year}년식)
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                    </CardContent>
                </Card>

                <Card sx={{ mb: 3 }}>
                    <CardContent>
                        <Typography variant="subtitle1" fontWeight={600} mb={2}>
                            차량 사진 업로드 (4면 필수)
                        </Typography>
                        <Grid container spacing={2}>
                            {[
                                { key: 'front', label: '전면' },
                                { key: 'rear', label: '후면' },
                                { key: 'left', label: '좌측' },
                                { key: 'right', label: '우측' },
                            ].map(({ key, label }) => (
                                <Grid item xs={6} key={key}>
                                    <ImageUploadZone label={label} onChange={handleImageChange(key)} />
                                </Grid>
                            ))}
                        </Grid>
                    </CardContent>
                </Card>

                <Button
                    variant="contained"
                    size="large"
                    fullWidth
                    onClick={handleSubmit}
                    disabled={!vehicleId || !allUploaded || loading}
                >
                    {loading ? (
                        <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
                            <CircularProgress size={20} color="inherit" />
                            AI 분석 중... (30초~1분 소요)
                        </Box>
                    ) : 'AI 분석 시작'}
                </Button>
            </Box>
        </Box>
    );
}
