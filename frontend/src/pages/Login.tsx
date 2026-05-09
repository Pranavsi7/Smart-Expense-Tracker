import React, { useState } from "react";
import { loginUser, registerUser } from "../api/api";

interface Props { onLogin: (token: string) => void; }

const Login: React.FC<Props> = ({ onLogin }) => {
  const [isRegister, setIsRegister] = useState(false);
  const [submitting, setSubmitting] = useState(false);
  const [form, setForm] = useState({ firstName:"", lastName:"", email:"", password:"" });
  const [error, setError] = useState("");

  const handle = (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [e.target.name]: e.target.value });

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (submitting) return;
    setError("");
    setSubmitting(true);
    try {
      const res = isRegister
        ? await registerUser(form.firstName, form.lastName, form.email, form.password)
        : await loginUser(form.email, form.password);
      onLogin(res.data.token);
    } catch (err: any) {
      const msg = err.response?.data?.message
        || err.response?.data
        || (err.response?.status === 401 ? "Invalid email or password" : "Something went wrong. Please try again.");
      setError(typeof msg === "string" ? msg : JSON.stringify(msg));
    } finally {
      setSubmitting(false);
    }
  };

  const inp: React.CSSProperties = {
    width:"100%", padding:"10px 14px", marginBottom:12,
    border:"1px solid #ddd", borderRadius:8, fontSize:14, boxSizing:"border-box",
  };

  return (
    <div style={{ minHeight:"100vh", background:"linear-gradient(135deg,#1e3a5f 0%,#0d2137 100%)", display:"flex", alignItems:"center", justifyContent:"center" }}>
      <div style={{ background:"#fff", borderRadius:16, padding:40, width:400, boxShadow:"0 20px 60px rgba(0,0,0,.3)" }}>
        <h1 style={{ textAlign:"center", color:"#1e3a5f", marginBottom:4, fontSize:24 }}>💰 Expense Tracker</h1>
        <p style={{ textAlign:"center", color:"#888", marginBottom:24, fontSize:14 }}>
          {isRegister ? "Create your account" : "Sign in to continue"}
        </p>

        {error && (
          <div style={{ background:"#fee2e2", color:"#dc2626", padding:"10px 14px", borderRadius:8, marginBottom:16, fontSize:14 }}>
            {error}
          </div>
        )}

        <form onSubmit={submit}>
          {isRegister && (
            <div style={{ display:"grid", gridTemplateColumns:"1fr 1fr", gap:8 }}>
              <input name="firstName" placeholder="First Name" value={form.firstName}
                onChange={handle} required style={inp} />
              <input name="lastName"  placeholder="Last Name"  value={form.lastName}
                onChange={handle} required style={inp} />
            </div>
          )}
          <input name="email" type="email" placeholder="Email" value={form.email}
            onChange={handle} required style={inp} />
          <input name="password" type="password" placeholder="Password" value={form.password}
            onChange={handle} required style={{ ...inp, marginBottom:20 }} />

          <button type="submit" disabled={submitting}
            style={{ width:"100%", padding:12, background: submitting ? "#9ca3af" : "#1e3a5f",
              color:"#fff", border:"none", borderRadius:8, fontSize:16, fontWeight:600,
              cursor: submitting ? "not-allowed" : "pointer", transition:"background .2s" }}>
            {submitting ? "Please wait…" : isRegister ? "Register" : "Login"}
          </button>
        </form>

        <p style={{ textAlign:"center", marginTop:16, color:"#666", fontSize:14 }}>
          {isRegister ? "Already have an account? " : "Don't have an account? "}
          <span onClick={() => { setIsRegister(!isRegister); setError(""); }}
            style={{ color:"#1e3a5f", cursor:"pointer", fontWeight:600 }}>
            {isRegister ? "Login" : "Register"}
          </span>
        </p>
      </div>
    </div>
  );
};

export default Login;
