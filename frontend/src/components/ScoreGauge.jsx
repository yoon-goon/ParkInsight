import { Box, CircularProgress, Typography } from '@mui/material';

export default function ScoreGauge({ score, label, size = 100 }) {
    const color = score >= 70 ? '#4caf50' : score >= 40 ? '#ff9800' : '#f44336';

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 1 }}>
            <Box sx={{ position: 'relative', display: 'inline-flex' }}>
                <CircularProgress
                    variant="determinate"
                    value={100}
                    size={size}
                    sx={{ color: 'rgba(255,255,255,0.1)', position: 'absolute' }}
                />
                <CircularProgress
                    variant="determinate"
                    value={score}
                    size={size}
                    sx={{ color }}
                />
                <Box sx={{
                    position: 'absolute', inset: 0,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                }}>
                    <Typography variant="h6" fontWeight={700} sx={{ color }}>
                        {score}
                    </Typography>
                </Box>
            </Box>
            <Typography variant="body2" color="text.secondary">{label}</Typography>
        </Box>
    );
}
