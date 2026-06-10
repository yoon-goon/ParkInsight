import { AppBar, Box, Button, Toolbar, Typography } from '@mui/material';
import { useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

export default function NavBar() {
    const navigate = useNavigate();
    const location = useLocation();
    const { logout, getUser } = useAuth();
    const user = getUser();

    const navItems = [
        { label: '차량', path: '/vehicles' },
        { label: '히스토리', path: '/history' },
        { label: 'AI 상담', path: '/chat' },
    ];

    return (
        <AppBar position="static" color="transparent" elevation={0}
            sx={{ borderBottom: '1px solid rgba(255,255,255,0.08)' }}>
            <Toolbar sx={{ gap: 1 }}>
                <Typography
                    variant="h6" fontWeight={700} color="primary"
                    sx={{ cursor: 'pointer', mr: 2 }}
                    onClick={() => navigate('/vehicles')}
                >
                    ParkInsight AI
                </Typography>

                <Box sx={{ display: 'flex', gap: 0.5, flex: 1 }}>
                    {navItems.map(({ label, path }) => (
                        <Button
                            key={path}
                            size="small"
                            onClick={() => navigate(path)}
                            sx={{
                                color: location.pathname.startsWith(path) ? 'primary.main' : 'text.secondary',
                                fontWeight: location.pathname.startsWith(path) ? 700 : 400,
                            }}
                        >
                            {label}
                        </Button>
                    ))}
                </Box>

                <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
                    {user && <Typography variant="body2" color="text.secondary">{user.name}</Typography>}
                    <Button variant="outlined" size="small" onClick={logout}>로그아웃</Button>
                </Box>
            </Toolbar>
        </AppBar>
    );
}
