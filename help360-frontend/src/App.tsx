import { useEffect } from "react";
import { Route, Routes, useLocation, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import { AppShell } from "@/components/app-shell";
import { Login } from "@/pages/Login";
import { Dashboard } from "@/pages/Dashboard";
import { MyTickets } from "@/pages/MyTickets";
import { Active } from "@/pages/Active";
import { AllTickets } from "@/pages/AllTickets";
import { NewTicket } from "@/pages/NewTicket";
import { Detalhes } from "@/pages/Detalhes";
import { AlterarSenha } from "@/pages/AlterarSenha";


function ScrollTop() {
  const { pathname } = useLocation();
  useEffect(() => { window.scrollTo(0, 0); }, [pathname]);
  return null;
}

function PrivateRoute({ children }: { children: React.ReactNode }) {
  const { usuario, carregando } = useAuth();
  if (carregando) return <div className="p-8 text-center">Carregando...</div>;
  return usuario ? <>{children}</> : <Navigate to="/login" />;
}

function AppContent() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/" element={<PrivateRoute><AppShell><Dashboard /></AppShell></PrivateRoute>} />
      <Route path="/meus-tickets" element={<PrivateRoute><AppShell><MyTickets /></AppShell></PrivateRoute>} />
      <Route path="/tickets-ativos" element={<PrivateRoute><AppShell><Active /></AppShell></PrivateRoute>} />
      <Route path="/tickets" element={<PrivateRoute><AppShell><AllTickets /></AppShell></PrivateRoute>} />
      <Route path="/novo-ticket" element={<PrivateRoute><AppShell><NewTicket /></AppShell></PrivateRoute>} />
      <Route path="/tickets/:id" element={<PrivateRoute><AppShell><Detalhes /></AppShell></PrivateRoute>} />
      <Route path="/alterar-senha" element={<PrivateRoute><AppShell><AlterarSenha /></AppShell></PrivateRoute>} />
    </Routes>
  );
}

export default function App() {
  return (
    <AuthProvider>
      <ScrollTop />
      <AppContent />
    </AuthProvider>
  );
}