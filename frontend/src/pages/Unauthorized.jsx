import { useNavigate } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

function Unauthorized() {
  const navigate = useNavigate();
  const { role } = useAuth();

  return (
    <div
      style={{
        minHeight: "100vh",
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        alignItems: "center",
        backgroundColor: "#f5f6fa",
        fontFamily: "Arial, sans-serif",
      }}
    >
      <h1 style={{ fontSize: "60px", margin: 0 }}>403</h1>

      <h2>Access Denied</h2>

      <p>
        Your role <strong>{role}</strong> does not have permission
        to access this page.
      </p>

      <button
        onClick={() => navigate(-1)}
        style={{
          marginTop: "20px",
          padding: "10px 20px",
          border: "none",
          borderRadius: "6px",
          backgroundColor: "#1976d2",
          color: "white",
          cursor: "pointer",
          fontSize: "15px",
        }}
      >
        Go Back
      </button>
    </div>
  );
}

export default Unauthorized;