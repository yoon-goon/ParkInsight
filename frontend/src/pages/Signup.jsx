import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Alert, Box, Button, Card, CardContent, TextField, Typography } from '@mui/material';
import { useAuth } from '../hooks/useAuth';

export default function Signup() {
    const { signup, loading, error } = useAuth();
    const [form, setForm] = useState({ name: '', email: '', password: '' });

    const handleChange = (e) => setForm(prev => ({ ...prev, [e.target.name]: e.target.value }));

    const handleSubmit = (e) => {
        e.preventDefault();
        signup(form);
    };

    return (
        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', minHeight: '100vh' }}>
            <Card sx={{ width: 400, p: 2 }}>
                <CardContent>
                    <Typography variant="h5" fontWeight={700} mb={1} color="primary">
                        ParkInsight AI
                    </Typography>
                    <Typography variant="body2" color="text.secondary" mb={3}>
                        차량 사진 분석 및 AI 코칭 플랫폼
                    </Typography>
                    <Typography variant="h6" mb={2}>회원가입</Typography>

                    {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}

                    <Box component="form" onSubmit={handleSubmit} sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
                        <TextField
                            name="name" label="이름"
                            value={form.name} onChange={handleChange}
                            required fullWidth
                        />
                        <TextField
                            name="email" label="이메일" type="email"
                            value={form.email} onChange={handleChange}
                            required fullWidth autoComplete="email"
                        />
                        <TextField
                            name="password" label="비밀번호 (8자 이상)" type="password"
                            value={form.password} onChange={handleChange}
                            required fullWidth autoComplete="new-password"
                        />
                        <Button type="submit" variant="contained" size="large" fullWidth disabled={loading}>
                            {loading ? '처리 중...' : '회원가입'}
                        </Button>
                        <Typography variant="body2" textAlign="center" color="text.secondary">
                            이미 계정이 있으신가요?{' '}
                            <Link to="/login" style={{ color: '#1565C0', textDecoration: 'none', fontWeight: 600 }}>
                                로그인
                            </Link>
                        </Typography>
                    </Box>
                </CardContent>
            </Card>
        </Box>
    );
}
