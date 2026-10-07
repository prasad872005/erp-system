import {
  AppBar,
  Box,
  Drawer,
  Toolbar,
  Typography,
  List,
  ListItemButton,
  ListItemIcon,
  ListItemText,
  Divider,
  Button,
  Chip,
} from "@mui/material";

import DashboardIcon from "@mui/icons-material/Dashboard";
import InventoryIcon from "@mui/icons-material/Inventory";
import PeopleIcon from "@mui/icons-material/People";
import ShoppingCartIcon from "@mui/icons-material/ShoppingCart";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import ReceiptLongIcon from "@mui/icons-material/ReceiptLong";
import AssignmentTurnedInIcon from "@mui/icons-material/AssignmentTurnedIn";
import LogoutIcon from "@mui/icons-material/Logout";

import { NavLink, Outlet } from "react-router-dom";
import { useAuth } from "../context/AuthContext";

const drawerWidth = 250;

function MainLayout() {
  const { username, role, logout } = useAuth();

  const menuItems = [
    {
      label: "Dashboard",
      path: "/dashboard",
      roles: ["ADMIN", "ACCOUNTANT"],
      icon: <DashboardIcon />,
    },
    {
      label: "Products",
      path: "/products",
      roles: ["ADMIN", "INVENTORY_MANAGER", "SALES_EXECUTIVE"],
      icon: <InventoryIcon />,
    },
    {
      label: "Customers",
      path: "/customers",
      roles: ["ADMIN", "SALES_EXECUTIVE"],
      icon: <PeopleIcon />,
    },
    {
      label: "Sales Orders",
      path: "/sales-orders",
      roles: ["ADMIN", "SALES_EXECUTIVE"],
      icon: <ShoppingCartIcon />,
    },
    {
      label: "Suppliers",
      path: "/suppliers",
      roles: ["ADMIN", "PURCHASE_MANAGER"],
      icon: <PeopleIcon />,
    },
    {
      label: "Purchase Orders",
      path: "/purchase-orders",
      roles: ["ADMIN", "PURCHASE_MANAGER"],
      icon: <LocalShippingIcon />,
    },
    {
      label: "GRN",
      path: "/grn",
      roles: ["ADMIN", "PURCHASE_MANAGER", "INVENTORY_MANAGER"],
      icon: <AssignmentTurnedInIcon />,
    },
    {
      label: "Invoices",
      path: "/invoices",
      roles: ["ADMIN", "SALES_EXECUTIVE", "ACCOUNTANT"],
      icon: <ReceiptLongIcon />,
    },
  ];

  const visibleMenuItems = menuItems.filter((item) =>
    item.roles.includes(role)
  );

  return (
    <Box sx={{ display: "flex", minHeight: "100vh" }}>

      {/* Top Bar */}
      <AppBar
        position="fixed"
        sx={{
          width: `calc(100% - ${drawerWidth}px)`,
          ml: `${drawerWidth}px`,
        }}
      >
        <Toolbar sx={{ justifyContent: "space-between" }}>

          <Typography variant="h6" component="div">
            ERP System
          </Typography>

          <Box sx={{ display: "flex", alignItems: "center", gap: 2 }}>
            <Typography variant="body2">
              {username}
            </Typography>

            <Chip
              label={role}
              size="small"
              sx={{
                color: "white",
                borderColor: "white",
              }}
              variant="outlined"
            />
          </Box>

        </Toolbar>
      </AppBar>

      {/* Sidebar */}
      <Drawer
        variant="permanent"
        sx={{
          width: drawerWidth,
          flexShrink: 0,
          "& .MuiDrawer-paper": {
            width: drawerWidth,
            boxSizing: "border-box",
          },
        }}
      >

        <Toolbar>
          <Typography
            variant="h6"
            fontWeight="bold"
          >
            ERP SYSTEM
          </Typography>
        </Toolbar>

        <Divider />

        <List sx={{ px: 1, py: 2 }}>

          {visibleMenuItems.map((item) => (
            <ListItemButton
              key={item.path}
              component={NavLink}
              to={item.path}
              sx={{
                borderRadius: 2,
                mb: 0.5,

                "&.active": {
                  backgroundColor: "primary.main",
                  color: "white",

                  "& .MuiListItemIcon-root": {
                    color: "white",
                  },

                  "&:hover": {
                    backgroundColor: "primary.dark",
                  },
                },
              }}
            >
              <ListItemIcon>
                {item.icon}
              </ListItemIcon>

              <ListItemText primary={item.label} />
            </ListItemButton>
          ))}

        </List>

        <Box sx={{ mt: "auto", p: 2 }}>

          <Divider sx={{ mb: 2 }} />

          <Button
            fullWidth
            variant="outlined"
            color="error"
            startIcon={<LogoutIcon />}
            onClick={logout}
          >
            Logout
          </Button>

        </Box>

      </Drawer>

      {/* Main Content */}
      <Box
        component="main"
        sx={{
          flexGrow: 1,
          p: 3,
          mt: 8,
          backgroundColor: "#f5f7fa",
          minHeight: "100vh",
        }}
      >
        <Outlet />
      </Box>

    </Box>
  );
}

export default MainLayout;