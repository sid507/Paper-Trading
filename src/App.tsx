import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import LoginScreen from './pages/LoginScreen';
import MarketFeedScreen from './pages/MarketFeedScreen';
import ProtectedRoute from './components/ProtectedRoute';

const App: React.FC = () => {
    return (
        <AuthProvider>
            <Router>
                <Routes>
                    <Route path="/login" element={<LoginScreen />} />
                    <Route 
                        path="/market-feed" 
                        element={
                            <ProtectedRoute>
                                <MarketFeedScreen />
                            </ProtectedRoute>
                        } 
                    />
                </Routes>
            </Router>
        </AuthProvider>
    );
};

export default App;