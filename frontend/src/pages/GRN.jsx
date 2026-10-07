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

import { Add } from "@mui/icons-material";

import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

import api from "../services/api";


const grnSchema = yup.object({
  purchaseOrderId: yup
    .number()
    .required("Purchase order is required"),

  receivedQuantity: yup
    .number()
    .typeError("Received quantity must be a number")
    .integer("Received quantity must be a whole number")
    .min(1, "Received quantity must be at least 1")
    .required("Received quantity is required"),
});


function GRN() {

  const [grns, setGrns] = useState([]);

  const [purchaseOrders, setPurchaseOrders] = useState([]);

  const [openDialog, setOpenDialog] = useState(false);

  const [message, setMessage] = useState("");

  const [error, setError] = useState("");


  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(grnSchema),
    defaultValues: {
      purchaseOrderId: "",
      receivedQuantity: 1,
    },
  });


  // Load GRNs
  const fetchGrns = async () => {

    try {

      const response = await api.get("/grns");

      setGrns(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load GRNs"
      );

    }
  };


  // Load purchase orders
  const fetchPurchaseOrders = async () => {

    try {

      const response = await api.get("/purchase-orders");

      setPurchaseOrders(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load purchase orders"
      );

    }
  };


  useEffect(() => {

    fetchGrns();
    fetchPurchaseOrders();

  }, []);


  // Open dialog
  const handleAdd = () => {

    reset({
      purchaseOrderId: "",
      receivedQuantity: 1,
    });

    setOpenDialog(true);
  };


  // Close dialog
  const handleClose = () => {

    setOpenDialog(false);

    reset({
      purchaseOrderId: "",
      receivedQuantity: 1,
    });
  };


  // Create GRN
  const onSubmit = async (data) => {

    try {

      await api.post("/grns", {
        purchaseOrderId: Number(data.purchaseOrderId),
        receivedQuantity: Number(data.receivedQuantity),
      });

      setMessage(
        "GRN created successfully. Product stock has been updated."
      );

      handleClose();

      fetchGrns();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to create GRN"
      );

    }
  };


  const getSupplierName = (supplier) => {

    if (!supplier) {
      return "Unknown";
    }

    return supplier.supplierName || "Unknown";
  };


  const getProductName = (product) => {

    if (!product) {
      return "Unknown";
    }

    return product.productName || "Unknown";
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
            Goods Received Notes
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Record received goods and update inventory stock
          </Typography>

        </Box>


        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleAdd}
        >
          Create GRN
        </Button>

      </Box>


      {/* GRN Table */}

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
                <strong>Purchase Order</strong>
              </TableCell>

              <TableCell>
                <strong>Supplier</strong>
              </TableCell>

              <TableCell>
                <strong>Product</strong>
              </TableCell>

              <TableCell>
                <strong>Received Quantity</strong>
              </TableCell>

              <TableCell>
                <strong>Received Date</strong>
              </TableCell>

            </TableRow>

          </TableHead>


          <TableBody>

            {grns.length === 0 ? (

              <TableRow>

                <TableCell
                  colSpan={6}
                  align="center"
                >
                  No GRNs found
                </TableCell>

              </TableRow>

            ) : (

              grns.map((grn) => (

                <TableRow
                  key={grn.id}
                  hover
                >

                  <TableCell>
                    {grn.id}
                  </TableCell>


                  <TableCell>
                    {grn.purchaseOrder?.id
                      ? `PO-${grn.purchaseOrder.id}`
                      : "Unknown"}
                  </TableCell>


                  <TableCell>
                    {getSupplierName(
                      grn.purchaseOrder?.supplier
                    )}
                  </TableCell>


                  <TableCell>
                    {getProductName(grn.product)}
                  </TableCell>


                  <TableCell>
                    {grn.receivedQuantity}
                  </TableCell>


                  <TableCell>
                    {grn.receivedDate}
                  </TableCell>

                </TableRow>

              ))

            )}

          </TableBody>

        </Table>

      </TableContainer>


      {/* Create GRN Dialog */}

      <Dialog
        open={openDialog}
        onClose={handleClose}
        fullWidth
        maxWidth="sm"
      >

        <DialogTitle>
          Create Goods Received Note
        </DialogTitle>


        <DialogContent>

          <Box
            component="form"
            id="grn-form"
            onSubmit={handleSubmit(onSubmit)}
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              mt: 1,
            }}
          >

            {/* Purchase Order */}

            <FormControl
              fullWidth
              error={!!errors.purchaseOrderId}
            >

              <InputLabel>
                Purchase Order
              </InputLabel>

              <Select
                label="Purchase Order"
                defaultValue=""
                {...register("purchaseOrderId")}
              >

                <MenuItem value="">
                  <em>Select Purchase Order</em>
                </MenuItem>


                {purchaseOrders.map((order) => (

                  <MenuItem
                    key={order.id}
                    value={order.id}
                  >

                    {`PO-${order.id}`}
                    {" | "}
                    {getSupplierName(order.supplier)}
                    {" | "}
                    {getProductName(order.product)}
                    {" | Ordered: "}
                    {order.quantity}

                  </MenuItem>

                ))}

              </Select>


              {errors.purchaseOrderId && (

                <Typography
                  variant="caption"
                  color="error"
                  sx={{ mt: 0.5 }}
                >
                  {errors.purchaseOrderId.message}
                </Typography>

              )}

            </FormControl>


            {/* Received Quantity */}

            <TextField
              label="Received Quantity"
              type="number"
              fullWidth
              inputProps={{
                min: 1,
                step: 1,
              }}
              {...register("receivedQuantity")}
              error={!!errors.receivedQuantity}
              helperText={
                errors.receivedQuantity?.message
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
            form="grn-form"
            variant="contained"
          >
            Create GRN
          </Button>

        </DialogActions>

      </Dialog>


      {/* Success Message */}

      <Snackbar
        open={!!message}
        autoHideDuration={4000}
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


export default GRN;