import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";

import Login from "./pages/Login";
import Unauthorized from "./pages/Unauthorized";
import Dashboard from "./pages/Dashboard";
import SalesOrders from "./pages/SalesOrders";
import Customers from "./pages/Customers";
import Products from "./pages/Products";

import ProtectedRoute from "./components/ProtectedRoute";
import MainLayout from "./layouts/MainLayout";
import Invoices from "./pages/Invoices";
import Suppliers from "./pages/Suppliers";
import PurchaseOrders from "./pages/PurchaseOrders";
import GRN from "./pages/GRN";

function App() {
  return (
    <BrowserRouter>
      <Routes>

        {/* ==================== PUBLIC ==================== */}

        <Route path="/login" element={<Login />} />

        <Route path="/unauthorized" element={<Unauthorized />} />


        {/* ==================== PROTECTED ==================== */}

        <Route
          element={
            <ProtectedRoute
              allowedRoles={[
                "ADMIN",
                "SALES_EXECUTIVE",
                "PURCHASE_MANAGER",
                "INVENTORY_MANAGER",
                "ACCOUNTANT",
              ]}
            >
              <MainLayout />
            </ProtectedRoute>
          }
        >

          {/* Dashboard */}
          <Route
            path="/dashboard"
            element={
              <ProtectedRoute
                allowedRoles={["ADMIN", "ACCOUNTANT"]}
              >
                <Dashboard />
              </ProtectedRoute>
            }
          />

          {/* Products */}
          <Route
            path="/products"
            element={
              <ProtectedRoute
                allowedRoles={[
                  "ADMIN",
                  "INVENTORY_MANAGER",
                  "SALES_EXECUTIVE",
                ]}
              >
                <Products />
              </ProtectedRoute>
            }
          />

          {/* Customers */}
          <Route
            path="/customers"
            element={
              <ProtectedRoute
                allowedRoles={["ADMIN", "SALES_EXECUTIVE"]}
              >
                <Customers />
              </ProtectedRoute>
            }
          />

          {/* Sales Orders */}
          <Route
            path="/sales-orders"
            element={
              <ProtectedRoute
                allowedRoles={["ADMIN", "SALES_EXECUTIVE"]}
              >
                <SalesOrders />
              </ProtectedRoute>
            }
          />

          {/* Suppliers */}
<Route
  path="/suppliers"
  element={
    <ProtectedRoute
      allowedRoles={[
        "ADMIN",
        "PURCHASE_MANAGER",
      ]}
    >
      <Suppliers />
    </ProtectedRoute>
  }
/>

      {/* Purchase Orders */}
<Route
  path="/purchase-orders"
  element={
    <ProtectedRoute
      allowedRoles={[
        "ADMIN",
        "PURCHASE_MANAGER",
      ]}
    >
      <PurchaseOrders />
    </ProtectedRoute>
  }
/>

        </Route>

            {/* Invoices */}
<Route
  path="/invoices"
  element={
    <ProtectedRoute
      allowedRoles={[
        "ADMIN",
        "SALES_EXECUTIVE",
        "ACCOUNTANT",
      ]}
    >
      <Invoices />
    </ProtectedRoute>
  }
/>

      {/* GRN */}
<Route
  path="/grn"
  element={
    <ProtectedRoute
      allowedRoles={[
        "ADMIN",
        "PURCHASE_MANAGER",
        "INVENTORY_MANAGER",
      ]}
    >
      <GRN />
    </ProtectedRoute>
  }
/>

        {/* ==================== DEFAULT ==================== */}

        <Route
          path="/"
          element={<Navigate to="/login" replace />}
        />

        <Route
          path="*"
          element={<Navigate to="/login" replace />}
        />

      </Routes>
    </BrowserRouter>
  );
}

export default App;