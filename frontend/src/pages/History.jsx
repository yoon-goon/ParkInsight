import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
    Box, Card, CardActionArea, CardContent, Chip, CircularProgress,
    Typography, Alert,
} from '@mui/material';
import NavBar from '../components/NavBar';
import { analysisApi } from '../api/analysisApi';

const RISK_COLOR = { HIGH: 'error', MEDIUM: 'warning', LOW: 'success' };

export default function History() {
    const navigate = useNavigate();

    const { data, isLoading, error } = useQuery({
        queryKey: ['history'],
        queryFn: () => analysisApi.getHistory(),
        select: (res) => res.data,
    });

    return (
        <Box>
            <NavBar />
            <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
                <Typography variant="h5" fontWeight={700} mb={3}>분석 히스토리</Typography>

                {error && <Alert severity="error">히스토리를 불러오지 못했습니다.</Alert>}

                {isLoading ? (
                    <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}>
                        <CircularProgress />
                    </Box>
                ) : data?.length === 0 ? (
                    <Typography color="text.secondary" textAlign="center" mt={8}>
                        분석 내역이 없습니다.
                    </Typography>
                ) : (
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        {data?.map((item) => (
                            <Card key={item.id}>
                                <CardActionArea onClick={() => navigate(`/analysis/${item.id}`)}>
                                    <CardContent sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                                        <img
                                            src={item.frontUrl}
                                            alt="front"
                                            style={{ width: 80, height: 60, objectFit: 'cover', borderRadius: 8 }}
                                        />
                                        <Box sx={{ flex: 1 }}>
                                            <Typography variant="body1" fontWeight={600}>
                                                분석 #{item.id}
                                            </Typography>
                                            <Typography variant="caption" color="text.secondary">
                                                {new Date(item.createdAt).toLocaleString('ko-KR')}
                                            </Typography>
                                            <Box sx={{ display: 'flex', gap: 1, mt: 0.5, flexWrap: 'wrap' }}>
                                                <Chip label={`주차 ${item.parkingScore}점`} size="small" />
                                                <Chip label={`세차 ${item.washScore}%`} size="small" variant="outlined" />
                                                <Chip
                                                    label={`문콕 ${item.doorDentRisk}`}
                                                    size="small"
                                                    color={RISK_COLOR[item.doorDentRisk] || 'default'}
                                                />
                                            </Box>
                                        </Box>
                                    </CardContent>
                                </CardActionArea>
                            </Card>
                        ))}
                    </Box>
                )}
            </Box>
        </Box>
    );
}
