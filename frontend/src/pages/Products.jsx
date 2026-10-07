import { useEffect, useState } from "react";

import {
  Alert,
  Box,
  Button,
  Chip,
  CircularProgress,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  IconButton,
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  TextField,
  Tooltip,
  Typography,
} from "@mui/material";

import AddIcon from "@mui/icons-material/Add";
import EditIcon from "@mui/icons-material/Edit";
import DeleteIcon from "@mui/icons-material/Delete";

import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

import api from "../services/api";


/* =========================
   Validation Schema
========================= */

const productSchema = yup.object({
  productName: yup
    .string()
    .trim()
    .required("Product name is required"),

  sku: yup
    .string()
    .trim()
    .required("SKU is required"),

  category: yup
    .string()
    .trim()
    .required("Category is required"),

  unitPrice: yup
    .number()
    .typeError("Unit price is required")
    .min(0, "Unit price cannot be negative")
    .required("Unit price is required"),

  currentStock: yup
    .number()
    .typeError("Current stock is required")
    .integer("Current stock must be a whole number")
    .min(0, "Current stock cannot be negative")
    .required("Current stock is required"),

  reorderLevel: yup
    .number()
    .typeError("Reorder level is required")
    .integer("Reorder level must be a whole number")
    .min(0, "Reorder level cannot be negative")
    .required("Reorder level is required"),
});


function Products() {

  const [products, setProducts] = useState([]);

  const [loading, setLoading] = useState(true);

  const [error, setError] = useState("");

  const [success, setSuccess] = useState("");

  const [openDialog, setOpenDialog] = useState(false);

  const [saving, setSaving] = useState(false);

  const [editingProductId, setEditingProductId] = useState(null);


  /* =========================
     React Hook Form
  ========================= */

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(productSchema),
    defaultValues: {
      productName: "",
      sku: "",
      category: "",
      unitPrice: "",
      currentStock: "",
      reorderLevel: "",
    },
  });


  /* =========================
     Load Products
  ========================= */

  useEffect(() => {
    fetchProducts();
  }, []);


  const fetchProducts = async () => {

    try {

      setLoading(true);

      const response = await api.get("/products");

      setProducts(response.data);

    } catch (err) {

      console.error(err);

      setError("Failed to load products.");

    } finally {

      setLoading(false);

    }
  };


  /* =========================
     Open Add Dialog
  ========================= */

  const handleOpenAddDialog = () => {

    setEditingProductId(null);

    setSuccess("");

    setError("");

    reset({
      productName: "",
      sku: "",
      category: "",
      unitPrice: "",
      currentStock: "",
      reorderLevel: "",
    });

    setOpenDialog(true);
  };


  /* =========================
     Open Edit Dialog
  ========================= */

  const handleOpenEditDialog = (product) => {

    setEditingProductId(product.id);

    setSuccess("");

    setError("");

    reset({
      productName: product.productName,
      sku: product.sku,
      category: product.category,
      unitPrice: product.unitPrice,
      currentStock: product.currentStock,
      reorderLevel: product.reorderLevel,
    });

    setOpenDialog(true);
  };


  /* =========================
     Close Dialog
  ========================= */

  const handleCloseDialog = () => {

    if (saving) {
      return;
    }

    setOpenDialog(false);

    setEditingProductId(null);

    reset();
  };


  /* =========================
     Create / Update Product
  ========================= */

  const onSubmit = async (data) => {

    try {

      setSaving(true);

      setError("");

      setSuccess("");

      const productData = {
        productName: data.productName,
        sku: data.sku,
        category: data.category,
        unitPrice: Number(data.unitPrice),
        currentStock: Number(data.currentStock),
        reorderLevel: Number(data.reorderLevel),
      };


      if (editingProductId) {

        await api.put(
          `/products/${editingProductId}`,
          productData
        );

        setSuccess("Product updated successfully.");

      } else {

        await api.post(
          "/products",
          productData
        );

        setSuccess("Product created successfully.");
      }


      setOpenDialog(false);

      setEditingProductId(null);

      reset();

      await fetchProducts();

    } catch (err) {

      console.error(err);

      if (err.response?.data?.message) {

        setError(err.response.data.message);

      } else {

        setError(
          editingProductId
            ? "Failed to update product."
            : "Failed to create product."
        );
      }

    } finally {

      setSaving(false);

    }
  };


  /* =========================
     Delete Product
  ========================= */

  const handleDelete = async (product) => {

    const confirmed = window.confirm(
      `Are you sure you want to delete "${product.productName}"?`
    );

    if (!confirmed) {
      return;
    }

    try {

      setError("");

      setSuccess("");

      await api.delete(`/products/${product.id}`);

      setSuccess("Product deleted successfully.");

      await fetchProducts();

    } catch (err) {

      console.error(err);

      if (err.response?.data?.message) {

        setError(err.response.data.message);

      } else {

        setError("Failed to delete product.");
      }
    }
  };


  /* =========================
     Loading
  ========================= */

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


  return (
    <Box>

      {/* =========================
          Page Header
      ========================= */}

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
            Products
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
            sx={{ mt: 0.5 }}
          >
            Manage your inventory products
          </Typography>

        </Box>


        <Button
          variant="contained"
          startIcon={<AddIcon />}
          onClick={handleOpenAddDialog}
        >
          Add Product
        </Button>

      </Box>


      {/* =========================
          Success Message
      ========================= */}

      {success && (

        <Alert
          severity="success"
          sx={{ mb: 2 }}
          onClose={() => setSuccess("")}
        >
          {success}
        </Alert>

      )}


      {/* =========================
          Error Message
      ========================= */}

      {error && (

        <Alert
          severity="error"
          sx={{ mb: 2 }}
          onClose={() => setError("")}
        >
          {error}
        </Alert>

      )}


      {/* =========================
          Products Table
      ========================= */}

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
                <strong>Product</strong>
              </TableCell>

              <TableCell>
                <strong>SKU</strong>
              </TableCell>

              <TableCell>
                <strong>Category</strong>
              </TableCell>

              <TableCell>
                <strong>Unit Price</strong>
              </TableCell>

              <TableCell>
                <strong>Stock</strong>
              </TableCell>

              <TableCell>
                <strong>Reorder Level</strong>
              </TableCell>

              <TableCell>
                <strong>Actions</strong>
              </TableCell>

            </TableRow>

          </TableHead>


          <TableBody>

            {products.length === 0 ? (

              <TableRow>

                <TableCell
                  colSpan={8}
                  align="center"
                >
                  No products found.
                </TableCell>

              </TableRow>

            ) : (

              products.map((product) => (

                <TableRow
                  key={product.id}
                  hover
                >

                  <TableCell>
                    {product.id}
                  </TableCell>


                  <TableCell>

                    <Typography fontWeight="500">
                      {product.productName}
                    </Typography>

                  </TableCell>


                  <TableCell>
                    {product.sku}
                  </TableCell>


                  <TableCell>
                    {product.category}
                  </TableCell>


                  <TableCell>
                    ₹
                    {Number(product.unitPrice).toLocaleString(
                      "en-IN"
                    )}
                  </TableCell>


                  <TableCell>

                    <Chip
                      label={product.currentStock}
                      color={
                        product.currentStock <=
                        product.reorderLevel
                          ? "error"
                          : "success"
                      }
                      size="small"
                    />

                  </TableCell>


                  <TableCell>
                    {product.reorderLevel}
                  </TableCell>


                  {/* Actions */}

                  <TableCell>

                    <Tooltip title="Edit Product">

                      <IconButton
                        color="primary"
                        onClick={() =>
                          handleOpenEditDialog(product)
                        }
                      >
                        <EditIcon />
                      </IconButton>

                    </Tooltip>


                    <Tooltip title="Delete Product">

                      <IconButton
                        color="error"
                        onClick={() =>
                          handleDelete(product)
                        }
                      >
                        <DeleteIcon />
                      </IconButton>

                    </Tooltip>

                  </TableCell>

                </TableRow>

              ))

            )}

          </TableBody>

        </Table>

      </TableContainer>


      {/* =========================
          Add / Edit Dialog
      ========================= */}

      <Dialog
        open={openDialog}
        onClose={handleCloseDialog}
        fullWidth
        maxWidth="sm"
      >

        <DialogTitle>

          {editingProductId
            ? "Edit Product"
            : "Add Product"}

        </DialogTitle>


        <Box
          component="form"
          onSubmit={handleSubmit(onSubmit)}
        >

          <DialogContent>

            <TextField
              {...register("productName")}
              label="Product Name"
              fullWidth
              margin="normal"
              error={!!errors.productName}
              helperText={errors.productName?.message}
            />


            <TextField
              {...register("sku")}
              label="SKU"
              fullWidth
              margin="normal"
              error={!!errors.sku}
              helperText={errors.sku?.message}
            />


            <TextField
              {...register("category")}
              label="Category"
              fullWidth
              margin="normal"
              error={!!errors.category}
              helperText={errors.category?.message}
            />


            <TextField
              {...register("unitPrice")}
              label="Unit Price"
              type="number"
              fullWidth
              margin="normal"
              inputProps={{
                min: 0,
                step: "0.01",
              }}
              error={!!errors.unitPrice}
              helperText={errors.unitPrice?.message}
            />


            <TextField
              {...register("currentStock")}
              label="Current Stock"
              type="number"
              fullWidth
              margin="normal"
              inputProps={{
                min: 0,
                step: 1,
              }}
              error={!!errors.currentStock}
              helperText={errors.currentStock?.message}
            />


            <TextField
              {...register("reorderLevel")}
              label="Reorder Level"
              type="number"
              fullWidth
              margin="normal"
              inputProps={{
                min: 0,
                step: 1,
              }}
              error={!!errors.reorderLevel}
              helperText={errors.reorderLevel?.message}
            />

          </DialogContent>


          <DialogActions sx={{ px: 3, pb: 2 }}>

            <Button
              onClick={handleCloseDialog}
              disabled={saving}
            >
              Cancel
            </Button>


            <Button
              type="submit"
              variant="contained"
              disabled={saving}
            >

              {saving
                ? "Saving..."
                : editingProductId
                ? "Update Product"
                : "Save Product"}

            </Button>

          </DialogActions>

        </Box>

      </Dialog>

    </Box>
  );
}

export default Products;