import { useRef, useState } from 'react';
import { Box, Typography } from '@mui/material';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';

export default function ImageUploadZone({ label, onChange }) {
    const [preview, setPreview] = useState(null);
    const inputRef = useRef();

    const handleFile = (file) => {
        if (!file) return;
        setPreview(URL.createObjectURL(file));
        onChange(file);
    };

    const handleDrop = (e) => {
        e.preventDefault();
        handleFile(e.dataTransfer.files[0]);
    };

    return (
        <Box
            onClick={() => inputRef.current.click()}
            onDrop={handleDrop}
            onDragOver={(e) => e.preventDefault()}
            sx={{
                border: '2px dashed',
                borderColor: preview ? 'primary.main' : 'rgba(255,255,255,0.2)',
                borderRadius: 2,
                height: 160,
                display: 'flex',
                flexDirection: 'column',
                alignItems: 'center',
                justifyContent: 'center',
                cursor: 'pointer',
                overflow: 'hidden',
                position: 'relative',
                transition: 'border-color 0.2s',
                '&:hover': { borderColor: 'primary.main' },
            }}
        >
            <input
                ref={inputRef}
                type="file"
                accept="image/*"
                style={{ display: 'none' }}
                onChange={(e) => handleFile(e.target.files[0])}
            />
            {preview ? (
                <img src={preview} alt={label} style={{ width: '100%', height: '100%', objectFit: 'cover' }} />
            ) : (
                <>
                    <CloudUploadIcon sx={{ fontSize: 36, color: 'text.secondary', mb: 1 }} />
                    <Typography variant="body2" color="text.secondary">{label}</Typography>
                </>
            )}
        </Box>
    );
}
