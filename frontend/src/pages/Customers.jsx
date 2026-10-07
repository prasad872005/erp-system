import { useEffect, useState } from "react";
import {
  Alert,
  Box,
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Paper,
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
  Edit,
} from "@mui/icons-material";

import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

import api from "../services/api";


// Validation schema
const customerSchema = yup.object({
  customerName: yup
    .string()
    .required("Customer name is required"),

  email: yup
    .string()
    .email("Invalid email format")
    .required("Email is required"),

  phone: yup
    .string()
    .required("Phone is required"),

  address: yup
    .string()
    .required("Address is required"),
});


function Customers() {

  const [customers, setCustomers] = useState([]);

  const [openDialog, setOpenDialog] = useState(false);

  const [editingCustomer, setEditingCustomer] = useState(null);

  const [deleteCustomerId, setDeleteCustomerId] = useState(null);

  const [message, setMessage] = useState("");

  const [error, setError] = useState("");


  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(customerSchema),
    defaultValues: {
      customerName: "",
      email: "",
      phone: "",
      address: "",
    },
  });


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


  useEffect(() => {
    fetchCustomers();
  }, []);


  // Open Add dialog
  const handleAdd = () => {

    setEditingCustomer(null);

    reset({
      customerName: "",
      email: "",
      phone: "",
      address: "",
    });

    setOpenDialog(true);
  };


  // Open Edit dialog
  const handleEdit = (customer) => {

    setEditingCustomer(customer);

    reset({
      customerName: customer.customerName,
      email: customer.email,
      phone: customer.phone,
      address: customer.address,
    });

    setOpenDialog(true);
  };


  // Close dialog
  const handleClose = () => {

    setOpenDialog(false);

    setEditingCustomer(null);

    reset();
  };


  // Create / Update customer
  const onSubmit = async (data) => {

    try {

      if (editingCustomer) {

        await api.put(
          `/customers/${editingCustomer.id}`,
          data
        );

        setMessage("Customer updated successfully");

      } else {

        await api.post(
          "/customers",
          data
        );

        setMessage("Customer added successfully");
      }


      handleClose();

      fetchCustomers();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to save customer"
      );

    }
  };


  // Delete customer
  const handleDelete = async () => {

    try {

      await api.delete(
        `/customers/${deleteCustomerId}`
      );

      setMessage("Customer deleted successfully");

      setDeleteCustomerId(null);

      fetchCustomers();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to delete customer"
      );

    }
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
            Customers
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Manage your customers
          </Typography>

        </Box>


        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleAdd}
        >
          Add Customer
        </Button>

      </Box>


      {/* Customer Table */}

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
                <strong>Customer Name</strong>
              </TableCell>

              <TableCell>
                <strong>Email</strong>
              </TableCell>

              <TableCell>
                <strong>Phone</strong>
              </TableCell>

              <TableCell>
                <strong>Address</strong>
              </TableCell>

              <TableCell align="center">
                <strong>Actions</strong>
              </TableCell>

            </TableRow>

          </TableHead>


          <TableBody>

            {customers.length === 0 ? (

              <TableRow>

                <TableCell
                  colSpan={6}
                  align="center"
                >
                  No customers found
                </TableCell>

              </TableRow>

            ) : (

              customers.map((customer) => (

                <TableRow
                  key={customer.id}
                  hover
                >

                  <TableCell>
                    {customer.id}
                  </TableCell>

                  <TableCell>
                    {customer.customerName}
                  </TableCell>

                  <TableCell>
                    {customer.email}
                  </TableCell>

                  <TableCell>
                    {customer.phone}
                  </TableCell>

                  <TableCell>
                    {customer.address}
                  </TableCell>

                  <TableCell align="center">

                    <IconButton
                      color="primary"
                      onClick={() =>
                        handleEdit(customer)
                      }
                    >
                      <Edit />
                    </IconButton>


                    <IconButton
                      color="error"
                      onClick={() =>
                        setDeleteCustomerId(
                          customer.id
                        )
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


      {/* Add / Edit Dialog */}

      <Dialog
        open={openDialog}
        onClose={handleClose}
        fullWidth
        maxWidth="sm"
      >

        <DialogTitle>

          {editingCustomer
            ? "Edit Customer"
            : "Add Customer"}

        </DialogTitle>


        <DialogContent>

          <Box
            component="form"
            id="customer-form"
            onSubmit={handleSubmit(onSubmit)}
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              mt: 1,
            }}
          >

            <TextField
              label="Customer Name"
              fullWidth
              {...register("customerName")}
              error={!!errors.customerName}
              helperText={
                errors.customerName?.message
              }
            />


            <TextField
              label="Email"
              type="email"
              fullWidth
              {...register("email")}
              error={!!errors.email}
              helperText={
                errors.email?.message
              }
            />


            <TextField
              label="Phone"
              fullWidth
              {...register("phone")}
              error={!!errors.phone}
              helperText={
                errors.phone?.message
              }
            />


            <TextField
              label="Address"
              fullWidth
              multiline
              rows={3}
              {...register("address")}
              error={!!errors.address}
              helperText={
                errors.address?.message
              }
            />

          </Box>

        </DialogContent>


        <DialogActions>

          <Button
            onClick={handleClose}
          >
            Cancel
          </Button>


          <Button
            type="submit"
            form="customer-form"
            variant="contained"
          >
            {editingCustomer
              ? "Update"
              : "Save"}
          </Button>

        </DialogActions>

      </Dialog>


      {/* Delete Confirmation */}

      <Dialog
        open={deleteCustomerId !== null}
        onClose={() =>
          setDeleteCustomerId(null)
        }
      >

        <DialogTitle>
          Delete Customer
        </DialogTitle>


        <DialogContent>

          <Typography>
            Are you sure you want to delete this
            customer?
          </Typography>

        </DialogContent>


        <DialogActions>

          <Button
            onClick={() =>
              setDeleteCustomerId(null)
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


      {/* Success Message */}

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


      {/* Error Message */}

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


export default Customers;