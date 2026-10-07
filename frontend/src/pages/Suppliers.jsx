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


const supplierSchema = yup.object({
  supplierName: yup
    .string()
    .required("Supplier name is required"),

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


function Suppliers() {

  const [suppliers, setSuppliers] = useState([]);

  const [openDialog, setOpenDialog] = useState(false);

  const [editingSupplier, setEditingSupplier] = useState(null);

  const [deleteSupplierId, setDeleteSupplierId] = useState(null);

  const [message, setMessage] = useState("");

  const [error, setError] = useState("");


  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(supplierSchema),
    defaultValues: {
      supplierName: "",
      email: "",
      phone: "",
      address: "",
    },
  });


  // Load suppliers
  const fetchSuppliers = async () => {

    try {

      const response = await api.get("/suppliers");

      setSuppliers(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load suppliers"
      );

    }
  };


  useEffect(() => {
    fetchSuppliers();
  }, []);


  // Open Add dialog
  const handleAdd = () => {

    setEditingSupplier(null);

    reset({
      supplierName: "",
      email: "",
      phone: "",
      address: "",
    });

    setOpenDialog(true);
  };


  // Open Edit dialog
  const handleEdit = (supplier) => {

    setEditingSupplier(supplier);

    reset({
      supplierName: supplier.supplierName,
      email: supplier.email,
      phone: supplier.phone,
      address: supplier.address,
    });

    setOpenDialog(true);
  };


  // Close dialog
  const handleClose = () => {

    setOpenDialog(false);

    setEditingSupplier(null);

    reset();
  };


  // Create / Update supplier
  const onSubmit = async (data) => {

    try {

      if (editingSupplier) {

        await api.put(
          `/suppliers/${editingSupplier.id}`,
          data
        );

        setMessage("Supplier updated successfully");

      } else {

        await api.post(
          "/suppliers",
          data
        );

        setMessage("Supplier added successfully");
      }

      handleClose();

      fetchSuppliers();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to save supplier"
      );

    }
  };


  // Delete supplier
  const handleDelete = async () => {

    try {

      await api.delete(
        `/suppliers/${deleteSupplierId}`
      );

      setMessage("Supplier deleted successfully");

      setDeleteSupplierId(null);

      fetchSuppliers();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to delete supplier"
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
            Suppliers
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Manage your suppliers
          </Typography>

        </Box>


        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleAdd}
        >
          Add Supplier
        </Button>

      </Box>


      {/* Supplier Table */}

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
                <strong>Supplier Name</strong>
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

            {suppliers.length === 0 ? (

              <TableRow>

                <TableCell
                  colSpan={6}
                  align="center"
                >
                  No suppliers found
                </TableCell>

              </TableRow>

            ) : (

              suppliers.map((supplier) => (

                <TableRow
                  key={supplier.id}
                  hover
                >

                  <TableCell>
                    {supplier.id}
                  </TableCell>

                  <TableCell>
                    {supplier.supplierName}
                  </TableCell>

                  <TableCell>
                    {supplier.email}
                  </TableCell>

                  <TableCell>
                    {supplier.phone}
                  </TableCell>

                  <TableCell>
                    {supplier.address}
                  </TableCell>

                  <TableCell align="center">

                    <IconButton
                      color="primary"
                      onClick={() =>
                        handleEdit(supplier)
                      }
                    >
                      <Edit />
                    </IconButton>


                    <IconButton
                      color="error"
                      onClick={() =>
                        setDeleteSupplierId(
                          supplier.id
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

          {editingSupplier
            ? "Edit Supplier"
            : "Add Supplier"}

        </DialogTitle>


        <DialogContent>

          <Box
            component="form"
            id="supplier-form"
            onSubmit={handleSubmit(onSubmit)}
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              mt: 1,
            }}
          >

            <TextField
              label="Supplier Name"
              fullWidth
              {...register("supplierName")}
              error={!!errors.supplierName}
              helperText={
                errors.supplierName?.message
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

          <Button onClick={handleClose}>
            Cancel
          </Button>


          <Button
            type="submit"
            form="supplier-form"
            variant="contained"
          >
            {editingSupplier
              ? "Update"
              : "Save"}
          </Button>

        </DialogActions>

      </Dialog>


      {/* Delete Confirmation */}

      <Dialog
        open={deleteSupplierId !== null}
        onClose={() =>
          setDeleteSupplierId(null)
        }
      >

        <DialogTitle>
          Delete Supplier
        </DialogTitle>


        <DialogContent>

          <Typography>
            Are you sure you want to delete this
            supplier?
          </Typography>

        </DialogContent>


        <DialogActions>

          <Button
            onClick={() =>
              setDeleteSupplierId(null)
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


export default Suppliers;