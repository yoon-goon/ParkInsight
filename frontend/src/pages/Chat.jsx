import { useState, useRef, useEffect } from 'react';
import {
    Box, Card, IconButton, InputAdornment, TextField,
    Typography, CircularProgress,
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import NavBar from '../components/NavBar';
import { chatApi } from '../api/chatApi';

export default function Chat() {
    const [messages, setMessages] = useState([
        { role: 'ai', text: '안녕하세요! 차량 관리에 대해 무엇이든 질문해주세요.' }
    ]);
    const [input, setInput] = useState('');
    const [loading, setLoading] = useState(false);
    const bottomRef = useRef();

    useEffect(() => {
        bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
    }, [messages]);

    const handleSend = async () => {
        const question = input.trim();
        if (!question || loading) return;

        setMessages(prev => [...prev, { role: 'user', text: question }]);
        setInput('');
        setLoading(true);

        try {
            const res = await chatApi.send({ question });
            setMessages(prev => [...prev, { role: 'ai', text: res.data.answer }]);
        } catch {
            setMessages(prev => [...prev, { role: 'ai', text: '응답을 가져오지 못했습니다. 잠시 후 다시 시도해주세요.' }]);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Box sx={{ display: 'flex', flexDirection: 'column', height: '100vh' }}>
            <NavBar />
            <Box sx={{ flex: 1, overflow: 'auto', p: 2, maxWidth: 700, mx: 'auto', width: '100%' }}>
                <Typography variant="h6" fontWeight={700} mb={2}>AI 차량 관리 상담</Typography>
                <Box sx={{ display: 'flex', flexDirection: 'column', gap: 1.5 }}>
                    {messages.map((msg, i) => (
                        <Box key={i} sx={{ display: 'flex', justifyContent: msg.role === 'user' ? 'flex-end' : 'flex-start' }}>
                            <Card sx={{
                                maxWidth: '80%',
                                bgcolor: msg.role === 'user' ? 'primary.main' : 'background.paper',
                            }}>
                                <Box sx={{ p: 1.5 }}>
                                    <Typography variant="body2" sx={{ whiteSpace: 'pre-wrap', lineHeight: 1.7 }}>
                                        {msg.text}
                                    </Typography>
                                </Box>
                            </Card>
                        </Box>
                    ))}
                    {loading && (
                        <Box sx={{ display: 'flex', justifyContent: 'flex-start' }}>
                            <Card sx={{ p: 1.5 }}>
                                <CircularProgress size={18} />
                            </Card>
                        </Box>
                    )}
                    <div ref={bottomRef} />
                </Box>
            </Box>

            <Box sx={{ p: 2, borderTop: '1px solid rgba(255,255,255,0.08)', maxWidth: 700, mx: 'auto', width: '100%' }}>
                <TextField
                    fullWidth
                    placeholder="차량 관리 질문을 입력하세요..."
                    value={input}
                    onChange={(e) => setInput(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && !e.shiftKey && handleSend()}
                    disabled={loading}
                    InputProps={{
                        endAdornment: (
                            <InputAdornment position="end">
                                <IconButton onClick={handleSend} disabled={!input.trim() || loading} color="primary">
                                    <SendIcon />
                                </IconButton>
                            </InputAdornment>
                        ),
                    }}
                />
            </Box>
        </Box>
    );
}
