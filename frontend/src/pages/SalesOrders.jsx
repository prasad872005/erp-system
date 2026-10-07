import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  FormControl,
  IconButton,
  InputLabel,
  MenuItem,
  Paper,
  Select,
  Snackbar,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Typography,
} from "@mui/material";

import {
  Add,
  Delete,
} from "@mui/icons-material";

import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

import api from "../services/api";


const salesOrderSchema = yup.object({
  customerId: yup
    .number()
    .required("Customer is required"),

  productId: yup
    .number()
    .required("Product is required"),

  quantity: yup
    .number()
    .typeError("Quantity must be a number")
    .integer("Quantity must be a whole number")
    .min(1, "Quantity must be at least 1")
    .required("Quantity is required"),
});


function SalesOrders() {

  const [orders, setOrders] = useState([]);

  const [customers, setCustomers] = useState([]);

  const [products, setProducts] = useState([]);

  const [openDialog, setOpenDialog] = useState(false);

  const [deleteOrderId, setDeleteOrderId] = useState(null);

  const [message, setMessage] = useState("");

  const [error, setError] = useState("");


  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(salesOrderSchema),
    defaultValues: {
      customerId: "",
      productId: "",
      quantity: 1,
    },
  });


  // Load sales orders
  const fetchOrders = async () => {

    try {

      const response = await api.get("/sales-orders");

      setOrders(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load sales orders"
      );

    }
  };


  // Load customers
  const fetchCustomers = async () => {

    try {

      const response = await api.get("/customers");

      setCustomers(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load customers"
      );

    }
  };


  // Load products
  const fetchProducts = async () => {

    try {

      const response = await api.get("/products");

      setProducts(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load products"
      );

    }
  };


  useEffect(() => {

    fetchOrders();
    fetchCustomers();
    fetchProducts();

  }, []);


  // Open create dialog
  const handleAdd = () => {

    reset({
      customerId: "",
      productId: "",
      quantity: 1,
    });

    setOpenDialog(true);
  };


  // Close dialog
  const handleClose = () => {

    setOpenDialog(false);

    reset({
      customerId: "",
      productId: "",
      quantity: 1,
    });
  };


  // Create sales order
  const onSubmit = async (data) => {

    try {

      await api.post("/sales-orders", {
        customerId: Number(data.customerId),
        productId: Number(data.productId),
        quantity: Number(data.quantity),
      });

      setMessage("Sales order created successfully");

      handleClose();

      fetchOrders();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to create sales order"
      );

    }
  };


  // Update order status
  const handleStatusChange = async (orderId, status) => {

    try {

      await api.put(
        `/sales-orders/${orderId}/status`,
        null,
        {
          params: {
            status: status,
          },
        }
      );

      setMessage("Order status updated successfully");

      fetchOrders();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to update order status"
      );

    }
  };


  // Delete sales order
  const handleDelete = async () => {

    try {

      await api.delete(
        `/sales-orders/${deleteOrderId}`
      );

      setMessage("Sales order deleted successfully");

      setDeleteOrderId(null);

      fetchOrders();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to delete sales order"
      );

    }
  };


  // Find customer name
  const getCustomerName = (customer) => {

    if (!customer) {
      return "Unknown";
    }

    return customer.customerName || "Unknown";

  };


  // Find product name
  const getProductName = (product) => {

    if (!product) {
      return "Unknown";
    }

    return product.productName || "Unknown";

  };


  // Format amount
  const formatAmount = (amount) => {

    if (amount === null || amount === undefined) {
      return "₹0.00";
    }

    return `₹${Number(amount).toFixed(2)}`;

  };


  return (

    <Box>

      {/* Header */}

      <Box
        sx={{
          display: "flex",
          justifyContent: "space-between",
          alignItems: "center",
          mb: 3,
        }}
      >

        <Box>

          <Typography
            variant="h4"
            fontWeight="bold"
          >
            Sales Orders
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Manage customer sales orders
          </Typography>

        </Box>


        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleAdd}
        >
          Create Sales Order
        </Button>

      </Box>


      {/* Sales Order Table */}

      <TableContainer
        component={Paper}
        elevation={2}
      >

        <Table>

          <TableHead>

            <TableRow>

              <TableCell>
                <strong>ID</strong>
              </TableCell>

              <TableCell>
                <strong>Customer</strong>
              </TableCell>

              <TableCell>
                <strong>Product</strong>
              </TableCell>

              <TableCell>
                <strong>Quantity</strong>
              </TableCell>

              <TableCell>
                <strong>Order Date</strong>
              </TableCell>

              <TableCell>
                <strong>Total Amount</strong>
              </TableCell>

              <TableCell>
                <strong>Status</strong>
              </TableCell>

              <TableCell align="center">
                <strong>Action</strong>
              </TableCell>

            </TableRow>

          </TableHead>


          <TableBody>

            {orders.length === 0 ? (

              <TableRow>

                <TableCell
                  colSpan={8}
                  align="center"
                >
                  No sales orders found
                </TableCell>

              </TableRow>

            ) : (

              orders.map((order) => (

                <TableRow
                  key={order.id}
                  hover
                >

                  <TableCell>
                    {order.id}
                  </TableCell>


                  <TableCell>
                    {getCustomerName(order.customer)}
                  </TableCell>


                  <TableCell>
                    {getProductName(order.product)}
                  </TableCell>


                  <TableCell>
                    {order.quantity}
                  </TableCell>


                  <TableCell>
                    {order.orderDate}
                  </TableCell>


                  <TableCell>
                    {formatAmount(order.totalAmount)}
                  </TableCell>


                  <TableCell>

                    <FormControl
                      size="small"
                      sx={{ minWidth: 140 }}
                    >

                      <Select
                        value={order.status || ""}
                        onChange={(event) =>
                          handleStatusChange(
                            order.id,
                            event.target.value
                          )
                        }
                      >

                        <MenuItem value="PENDING">
                          PENDING
                        </MenuItem>

                        <MenuItem value="CONFIRMED">
                          CONFIRMED
                        </MenuItem>

                        <MenuItem value="SHIPPED">
                          SHIPPED
                        </MenuItem>

                        <MenuItem value="DELIVERED">
                          DELIVERED
                        </MenuItem>

                        <MenuItem value="CANCELLED">
                          CANCELLED
                        </MenuItem>

                      </Select>

                    </FormControl>

                  </TableCell>


                  <TableCell align="center">

                    <IconButton
                      color="error"
                      onClick={() =>
                        setDeleteOrderId(order.id)
                      }
                    >

                      <Delete />

                    </IconButton>

                  </TableCell>

                </TableRow>

              ))

            )}

          </TableBody>

        </Table>

      </TableContainer>


      {/* Create Sales Order Dialog */}

      <Dialog
        open={openDialog}
        onClose={handleClose}
        fullWidth
        maxWidth="sm"
      >

        <DialogTitle>
          Create Sales Order
        </DialogTitle>


        <DialogContent>

          <Box
            component="form"
            id="sales-order-form"
            onSubmit={handleSubmit(onSubmit)}
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              mt: 1,
            }}
          >

            {/* Customer */}

            <FormControl
              fullWidth
              error={!!errors.customerId}
            >

              <InputLabel>
                Customer
              </InputLabel>

              <Select
                label="Customer"
                defaultValue=""
                {...register("customerId")}
              >

                <MenuItem value="">
                  <em>Select Customer</em>
                </MenuItem>

                {customers.map((customer) => (

                  <MenuItem
                    key={customer.id}
                    value={customer.id}
                  >
                    {customer.customerName}
                  </MenuItem>

                ))}

              </Select>

              {errors.customerId && (

                <Typography
                  variant="caption"
                  color="error"
                  sx={{ mt: 0.5 }}
                >
                  {errors.customerId.message}
                </Typography>

              )}

            </FormControl>


            {/* Product */}

            <FormControl
              fullWidth
              error={!!errors.productId}
            >

              <InputLabel>
                Product
              </InputLabel>

              <Select
                label="Product"
                defaultValue=""
                {...register("productId")}
              >

                <MenuItem value="">
                  <em>Select Product</em>
                </MenuItem>

                {products.map((product) => (

                  <MenuItem
                    key={product.id}
                    value={product.id}
                  >

                    {product.productName}
                    {" - ₹"}
                    {Number(product.unitPrice).toFixed(2)}
                    {" | Stock: "}
                    {product.currentStock}

                  </MenuItem>

                ))}

              </Select>

              {errors.productId && (

                <Typography
                  variant="caption"
                  color="error"
                  sx={{ mt: 0.5 }}
                >
                  {errors.productId.message}
                </Typography>

              )}

            </FormControl>


            {/* Quantity */}

            <TextField
              label="Quantity"
              type="number"
              fullWidth
              inputProps={{
                min: 1,
                step: 1,
              }}
              {...register("quantity")}
              error={!!errors.quantity}
              helperText={
                errors.quantity?.message
              }
            />

          </Box>

        </DialogContent>


        <DialogActions>

          <Button onClick={handleClose}>
            Cancel
          </Button>


          <Button
            type="submit"
            form="sales-order-form"
            variant="contained"
          >
            Create Order
          </Button>

        </DialogActions>

      </Dialog>


      {/* Delete Confirmation */}

      <Dialog
        open={deleteOrderId !== null}
        onClose={() =>
          setDeleteOrderId(null)
        }
      >

        <DialogTitle>
          Delete Sales Order
        </DialogTitle>


        <DialogContent>

          <Typography>
            Are you sure you want to delete this
            sales order?
          </Typography>

        </DialogContent>


        <DialogActions>

          <Button
            onClick={() =>
              setDeleteOrderId(null)
            }
          >
            Cancel
          </Button>


          <Button
            color="error"
            variant="contained"
            onClick={handleDelete}
          >
            Delete
          </Button>

        </DialogActions>

      </Dialog>


      {/* Success Snackbar */}

      <Snackbar
        open={!!message}
        autoHideDuration={3000}
        onClose={() => setMessage("")}
      >

        <Alert
          severity="success"
          onClose={() => setMessage("")}
        >
          {message}
        </Alert>

      </Snackbar>


      {/* Error Snackbar */}

      <Snackbar
        open={!!error}
        autoHideDuration={4000}
        onClose={() => setError("")}
      >

        <Alert
          severity="error"
          onClose={() => setError("")}
        >
          {error}
        </Alert>

      </Snackbar>

    </Box>

  );
}


export default SalesOrders;