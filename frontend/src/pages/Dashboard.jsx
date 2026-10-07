import { useEffect, useState } from "react";

import {
  Alert,
  Box,
  Card,
  CardContent,
  CircularProgress,
  Grid,
  Typography,
} from "@mui/material";

import InventoryIcon from "@mui/icons-material/Inventory";
import WarningIcon from "@mui/icons-material/Warning";
import PeopleIcon from "@mui/icons-material/People";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import TrendingUpIcon from "@mui/icons-material/TrendingUp";
import PaymentsIcon from "@mui/icons-material/Payments";

import {
  ResponsiveContainer,
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  Legend,
  PieChart,
  Pie,
  Cell,
} from "recharts";

import api from "../services/api";

function Dashboard() {
  const [dashboard, setDashboard] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    fetchDashboard();
  }, []);

  const fetchDashboard = async () => {
    try {
      const response = await api.get("/dashboard");
      setDashboard(response.data);
    } catch (err) {
      console.error(err);
      setError("Failed to load dashboard.");
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <Box
        sx={{
          display: "flex",
          justifyContent: "center",
          mt: 5,
        }}
      >
        <CircularProgress />
      </Box>
    );
  }

  if (!dashboard) {
    return (
      <Alert severity="error">
        Dashboard data is unavailable.
      </Alert>
    );
  }

  const cards = [
    {
      title: "Total Products",
      value: dashboard.totalProducts,
      icon: <InventoryIcon fontSize="large" />,
    },
    {
      title: "Low Stock Products",
      value: dashboard.lowStockProducts,
      icon: <WarningIcon fontSize="large" />,
    },
    {
      title: "Total Customers",
      value: dashboard.totalCustomers,
      icon: <PeopleIcon fontSize="large" />,
    },
    {
      title: "Total Suppliers",
      value: dashboard.totalSuppliers,
      icon: <LocalShippingIcon fontSize="large" />,
    },
    {
      title: "Sales Orders",
      value: dashboard.totalSalesOrders,
      icon: <ShoppingCartIcon fontSize="large" />,
    },
    {
      title: "Purchase Orders",
      value: dashboard.totalPurchaseOrders,
      icon: <LocalShippingIcon fontSize="large" />,
    },
    {
      title: "Total Invoices",
      value: dashboard.totalInvoices,
      icon: <ReceiptLongIcon fontSize="large" />,
    },
    {
      title: "Total Sales",
      value: `₹${dashboard.totalSalesAmount || 0}`,
      icon: <TrendingUpIcon fontSize="large" />,
    },
    {
      title: "Total Purchases",
      value: `₹${dashboard.totalPurchaseAmount || 0}`,
      icon: <PaymentsIcon fontSize="large" />,
    },
  ];

  // Sales vs Purchase chart
  const financialData = [
    {
      name: "Sales",
      amount: Number(dashboard.totalSalesAmount || 0),
    },
    {
      name: "Purchases",
      amount: Number(dashboard.totalPurchaseAmount || 0),
    },
  ];

  // Order distribution chart
  const orderData = [
    {
      name: "Sales Orders",
      value: dashboard.totalSalesOrders,
    },
    {
      name: "Purchase Orders",
      value: dashboard.totalPurchaseOrders,
    },
    {
      name: "Invoices",
      value: dashboard.totalInvoices,
    },
  ];

  return (
    <Box>
      {/* Header */}
      <Box sx={{ mb: 4 }}>
        <Typography variant="h4" fontWeight="bold">
          Dashboard
        </Typography>

        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ mt: 0.5 }}
        >
          Overview of your ERP system
        </Typography>
      </Box>

      {/* Error */}
      {error && (
        <Alert severity="error" sx={{ mb: 3 }}>
          {error}
        </Alert>
      )}

      {/* Summary Cards */}
      <Grid container spacing={3}>
        {cards.map((card) => (
          <Grid
            item
            xs={12}
            sm={6}
            md={4}
            lg={3}
            key={card.title}
          >
            <Card
              elevation={2}
              sx={{
                height: "100%",
                borderRadius: 2,
              }}
            >
              <CardContent>
                <Box
                  sx={{
                    display: "flex",
                    justifyContent: "space-between",
                    alignItems: "center",
                  }}
                >
                  <Box>
                    <Typography
                      variant="body2"
                      color="text.secondary"
                    >
                      {card.title}
                    </Typography>

                    <Typography
                      variant="h5"
                      fontWeight="bold"
                      sx={{ mt: 1 }}
                    >
                      {card.value}
                    </Typography>
                  </Box>

                  <Box sx={{ color: "primary.main" }}>
                    {card.icon}
                  </Box>
                </Box>
              </CardContent>
            </Card>
          </Grid>
        ))}
      </Grid>

      {/* Charts */}
      <Grid
        container
        spacing={3}
        sx={{ mt: 1 }}
      >
        {/* Sales vs Purchases */}
        <Grid item xs={12} md={7}>
          <Card elevation={2}>
            <CardContent>
              <Typography
                variant="h6"
                fontWeight="bold"
                sx={{ mb: 3 }}
              >
                Sales vs Purchases
              </Typography>

              <Box sx={{ width: "100%", height: 350 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <BarChart data={financialData}>
                    <CartesianGrid strokeDasharray="3 3" />

                    <XAxis dataKey="name" />

                    <YAxis />

                    <Tooltip
                      formatter={(value) =>
                        `₹${Number(value).toLocaleString("en-IN")}`
                      }
                    />

                    <Legend />

                    <Bar
                      dataKey="amount"
                      name="Amount"
                    />
                  </BarChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>

        {/* Order Distribution */}
        <Grid item xs={12} md={5}>
          <Card elevation={2}>
            <CardContent>
              <Typography
                variant="h6"
                fontWeight="bold"
                sx={{ mb: 3 }}
              >
                Order Summary
              </Typography>

              <Box sx={{ width: "100%", height: 350 }}>
                <ResponsiveContainer width="100%" height="100%">
                  <PieChart>
                    <Pie
                      data={orderData}
                      dataKey="value"
                      nameKey="name"
                      cx="50%"
                      cy="50%"
                      outerRadius={110}
                      label
                    >
                      {orderData.map((entry, index) => (
                        <Cell key={`cell-${index}`} />
                      ))}
                    </Pie>

                    <Tooltip />

                    <Legend />
                  </PieChart>
                </ResponsiveContainer>
              </Box>
            </CardContent>
          </Card>
        </Grid>
      </Grid>
    </Box>
  );
}

export default Dashboard;