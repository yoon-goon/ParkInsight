import { Navigate, Route, Routes } from 'react-router-dom';
import Login from './pages/Login';
import Signup from './pages/Signup';
import Vehicles from './pages/Vehicles';
import NewAnalysis from './pages/NewAnalysis';
import AnalysisResult from './pages/AnalysisResult';
import History from './pages/History';
import Chat from './pages/Chat';

function PrivateRoute({ children }) {
    return localStorage.getItem('token') ? children : <Navigate to="/login" replace />;
}

export default function App() {
    return (
        <Routes>
            <Route path="/login" element={<Login />} />
            <Route path="/signup" element={<Signup />} />
            <Route path="/vehicles" element={<PrivateRoute><Vehicles /></PrivateRoute>} />
            <Route path="/analysis/new" element={<PrivateRoute><NewAnalysis /></PrivateRoute>} />
            <Route path="/analysis/:id" element={<PrivateRoute><AnalysisResult /></PrivateRoute>} />
            <Route path="/history" element={<PrivateRoute><History /></PrivateRoute>} />
            <Route path="/chat" element={<PrivateRoute><Chat /></PrivateRoute>} />
            <Route path="/" element={<Navigate to="/vehicles" replace />} />
            <Route path="*" element={<Navigate to="/vehicles" replace />} />
        </Routes>
    );
}
