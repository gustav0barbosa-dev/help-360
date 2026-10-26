import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { LayoutDashboard, User, Activity, List, Plus, BookOpen, Bell, Search, LogOut, ChevronDown, Settings, KeyRound } from "lucide-react";
import { useAuth } from "../context/AuthContext";
import { cn } from "../lib/utils";

const NAV = [
  { to: "/", label: "Dashboard", icon: LayoutDashboard },
  { to: "/meus-tickets", label: "Meus Tickets", icon: User },
  { to: "/tickets-ativos", label: "Tickets Ativos", icon: Activity },
  { to: "/tickets", label: "Todos os Tickets", icon: List },
  { to: "/novo-ticket", label: "Novo Ticket", icon: Plus },
  { to: "/manual", label: "Manual", icon: BookOpen },
];

export function AppShell({ children }: { children: React.ReactNode }) {
  const { usuario, logout } = useAuth();
  const navigate = useNavigate();
  const [menuAberto, setMenuAberto] = useState(false);

  const iniciais = usuario?.nome?.split(" ").map(n => n[0]).slice(0, 2).join("").toUpperCase() || "US";

  function handleLogout() {
    logout();
    navigate("/login");
  }

  return (
    <div className="flex min-h-screen">
      <aside className="w-64 bg-[#172B3A] text-slate-300 flex flex-col sticky top-0 h-screen">
        <div className="flex items-center gap-3 p-4 text-white border-b border-white/10">
          <div className="bg-[#0F766E] rounded-lg p-2"><LayoutDashboard className="w-5 h-5" /></div>
          <span className="font-semibold">ServiceDesk TI</span>
        </div>

        {/* Bloco de Perfil (clicável) */}
        <div className="relative border-b border-white/10">
          <button
            onClick={() => setMenuAberto(!menuAberto)}
            className="w-full flex items-center gap-3 p-4 hover:bg-white/5 transition text-left"
          >
            <div className="w-9 h-9 rounded-full bg-[#2B4558] flex items-center justify-center text-white font-semibold text-sm">
              {iniciais}
            </div>
            <div className="min-w-0 flex-1">
              <p className="text-white font-medium truncate text-sm">{usuario?.nome || "Usuário"}</p>
              <p className="text-xs text-slate-400 truncate">{usuario?.perfil || ""}</p>
            </div>
            <ChevronDown className={cn("w-4 h-4 text-slate-400 transition-transform", menuAberto && "rotate-180")} />
          </button>

          {/* Dropdown */}
          {menuAberto && (
            <div className="absolute top-full left-0 w-full bg-[#1F3A4D] border-t border-white/10 shadow-lg z-30">
              <button
                onClick={() => { setMenuAberto(false); alert("Funcionalidade 'Meu Perfil' em desenvolvimento"); }}
                className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-slate-300 hover:bg-white/10 hover:text-white transition text-left"
              >
                <Settings className="w-4 h-4" /> Meu Perfil
              </button>
              <button
                onClick={() => { setMenuAberto(false); navigate("/alterar-senha"); }}
                className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-slate-300 hover:bg-white/10 hover:text-white transition text-left"
              >
                <KeyRound className="w-4 h-4" /> Alterar Senha
              </button>
              <button
                onClick={handleLogout}
                className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-red-400 hover:bg-red-500/20 hover:text-red-300 transition text-left border-t border-white/10"
              >
                <LogOut className="w-4 h-4" /> Sair
              </button>
            </div>
          )}
        </div>

        <nav className="flex-1 py-2">
          {NAV.map(({ to, label, icon: Icon }) => (
            <NavLink key={to} to={to} end={to === "/"}
              className={({ isActive }) => cn("flex items-center gap-3 px-4 py-2.5 border-l-[3px] transition text-sm",
                isActive ? "bg-white/10 text-white border-[#2DD4BF]" : "border-transparent hover:bg-white/5")}>
              <Icon className="w-5 h-5" />
              <span>{label}</span>
            </NavLink>
          ))}
        </nav>

        <button onClick={handleLogout} className="flex items-center gap-3 px-4 py-3 border-t border-white/10 hover:bg-white/5 transition text-left">
          <LogOut className="w-5 h-5" /><span>Sair</span>
        </button>
      </aside>

      <div className="flex-1 min-w-0">
        <header className="bg-white border-b border-slate-200 px-6 py-3 flex items-center gap-4 sticky top-0 z-20">
          <div className="flex-1 text-slate-500 text-sm">ServiceDesk TI / <b className="text-slate-800">Sistema</b></div>
          <div className="hidden md:flex items-center gap-2 border border-slate-300 rounded-lg px-3 w-64">
            <Search className="w-4 h-4 text-slate-400" />
            <input placeholder="Buscar (Enter)" className="flex-1 py-1.5 outline-none text-sm" />
          </div>
          <button className="relative p-2 rounded-lg hover:bg-slate-100">
            <Bell className="w-5 h-5 text-slate-500" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 bg-red-500 rounded-full border-2 border-white" />
          </button>
          {/* Avatar removido daqui */}
        </header>
        <main className="p-6 max-w-7xl mx-auto">{children}</main>
      </div>
    </div>
  );
}