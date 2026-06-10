import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import {
    Box, Button, Card, CardContent, Chip, CircularProgress,
    Divider, Grid, Typography, Alert,
} from '@mui/material';
import ChatIcon from '@mui/icons-material/Chat';
import NavBar from '../components/NavBar';
import ScoreGauge from '../components/ScoreGauge';
import { analysisApi } from '../api/analysisApi';

const RISK_COLOR = { HIGH: 'error', MEDIUM: 'warning', LOW: 'success' };
const RISK_LABEL = { HIGH: '높음', MEDIUM: '보통', LOW: '낮음' };

export default function AnalysisResult() {
    const { id } = useParams();
    const navigate = useNavigate();

    const { data, isLoading, error } = useQuery({
        queryKey: ['analysis', id],
        queryFn: () => analysisApi.getAnalysis(id),
        select: (res) => res.data,
    });

    const weather = (() => {
        try { return JSON.parse(data?.weatherSnapshot || '{}'); }
        catch { return {}; }
    })();

    if (isLoading) return (
        <Box><NavBar />
            <Box sx={{ display: 'flex', justifyContent: 'center', mt: 8 }}><CircularProgress /></Box>
        </Box>
    );

    if (error) return (
        <Box><NavBar />
            <Box sx={{ p: 3 }}><Alert severity="error">분석 결과를 불러오지 못했습니다.</Alert></Box>
        </Box>
    );

    return (
        <Box>
            <NavBar />
            <Box sx={{ p: 3, maxWidth: 800, mx: 'auto' }}>
                <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
                    <Typography variant="h5" fontWeight={700}>분석 결과</Typography>
                    <Button variant="outlined" startIcon={<ChatIcon />} onClick={() => navigate('/chat')}>
                        AI에게 질문하기
                    </Button>
                </Box>

                {/* Scores */}
                <Card sx={{ mb: 2 }}>
                    <CardContent>
                        <Typography variant="subtitle1" fontWeight={600} mb={3}>종합 점수</Typography>
                        <Box sx={{ display: 'flex', justifyContent: 'space-around', flexWrap: 'wrap', gap: 2 }}>
                            <ScoreGauge score={data.parkingScore} label="주차 점수" />
                            <ScoreGauge score={data.washScore} label="세차 필요도" />
                            <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1 }}>
                                <Chip
                                    label={`문콕 위험 ${RISK_LABEL[data.doorDentRisk] || data.doorDentRisk}`}
                                    color={RISK_COLOR[data.doorDentRisk] || 'default'}
                                    sx={{ fontSize: 14, px: 1, height: 40 }}
                                />
                                <Typography variant="body2" color="text.secondary">문콕 위험도</Typography>
                            </Box>
                        </Box>
                    </CardContent>
                </Card>

                {/* Images */}
                <Card sx={{ mb: 2 }}>
                    <CardContent>
                        <Typography variant="subtitle1" fontWeight={600} mb={2}>촬영 사진</Typography>
                        <Grid container spacing={1}>
                            {[
                                { url: data.frontUrl, label: '전면' },
                                { url: data.rearUrl, label: '후면' },
                                { url: data.leftUrl, label: '좌측' },
                                { url: data.rightUrl, label: '우측' },
                            ].map(({ url, label }) => (
                                <Grid item xs={6} key={label}>
                                    <Box sx={{ position: 'relative', borderRadius: 1, overflow: 'hidden' }}>
                                        <img src={url} alt={label} style={{ width: '100%', height: 140, objectFit: 'cover' }} />
                                        <Chip label={label} size="small"
                                            sx={{ position: 'absolute', top: 6, left: 6, opacity: 0.9 }} />
                                    </Box>
                                </Grid>
                            ))}
                        </Grid>
                    </CardContent>
                </Card>

                {/* Weather */}
                {weather.description && (
                    <Card sx={{ mb: 2 }}>
                        <CardContent>
                            <Typography variant="subtitle1" fontWeight={600} mb={1}>날씨 정보</Typography>
                            <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
                                <Chip label={`${weather.temperature?.toFixed(1)}°C`} size="small" />
                                <Chip label={`강수 ${weather.rainProbability}%`} size="small" variant="outlined" />
                                <Chip label={`미세먼지 ${weather.fineDust}`} size="small" variant="outlined" />
                            </Box>
                            {weather.recommendation && (
                                <Typography variant="body2" color="text.secondary" mt={1}>
                                    {weather.recommendation}
                                </Typography>
                            )}
                        </CardContent>
                    </Card>
                )}

                {/* Report */}
                <Card>
                    <CardContent>
                        <Typography variant="subtitle1" fontWeight={600} mb={1}>AI 리포트</Typography>
                        <Divider sx={{ mb: 2 }} />
                        <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.8 }}>
                            {data.reportText}
                        </Typography>
                    </CardContent>
                </Card>
            </Box>
        </Box>
    );
}
