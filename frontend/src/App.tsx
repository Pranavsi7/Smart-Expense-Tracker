import React, { useState } from "react";
import Login from "./pages/Login";
import Dashboard from "./components/Dashboard";

const App: React.FC = () => {
  const [token, setToken] = useState<string | null>(localStorage.getItem("jwt_token"));

  const handleLogin = (t: string) => {
    localStorage.setItem("jwt_token", t);
    setToken(t);
  };

  const handleLogout = () => {
    localStorage.removeItem("jwt_token");
    setToken(null);
  };

  return token ? <Dashboard onLogout={handleLogout} /> : <Login onLogin={handleLogin} />;
};

export default App;
