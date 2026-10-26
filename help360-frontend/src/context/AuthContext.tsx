import { createContext, useState, useEffect, useContext } from "react";
import { AuthAPI, type UsuarioAPI } from "../api/api";

type AuthContextType = {
  usuario: UsuarioAPI | null;
  login: (email: string, senha: string) => Promise<void>;
  logout: () => void;
  carregando: boolean;
};

const AuthContext = createContext<AuthContextType>({} as AuthContextType);

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [usuario, setUsuario] = useState<UsuarioAPI | null>(null);
  const [carregando, setCarregando] = useState(true);

  useEffect(() => {
    const salvo = localStorage.getItem("help360_usuario");
    if (salvo) setUsuario(JSON.parse(salvo));
    setCarregando(false);
  }, []);

  async function login(email: string, senha: string) {
    const { data } = await AuthAPI.login(email, senha);
    localStorage.setItem("help360_token", data.token);
    localStorage.setItem("help360_usuario", JSON.stringify(data.usuario));
    setUsuario(data.usuario);
  }

  function logout() {
    localStorage.removeItem("help360_token");
    localStorage.removeItem("help360_usuario");
    setUsuario(null);
  }

  return (
    <AuthContext.Provider value={{ usuario, login, logout, carregando }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  return useContext(AuthContext);
}