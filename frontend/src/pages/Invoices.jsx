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
  Typography,
} from "@mui/material";

import {
  Add,
  Download,
} from "@mui/icons-material";

import { useForm } from "react-hook-form";
import { yupResolver } from "@hookform/resolvers/yup";
import * as yup from "yup";

import api from "../services/api";


const invoiceSchema = yup.object({
  salesOrderId: yup
    .number()
    .required("Sales order is required"),
});


function Invoices() {

  const [invoices, setInvoices] = useState([]);

  const [salesOrders, setSalesOrders] = useState([]);

  const [openDialog, setOpenDialog] = useState(false);

  const [message, setMessage] = useState("");

  const [error, setError] = useState("");


  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm({
    resolver: yupResolver(invoiceSchema),
    defaultValues: {
      salesOrderId: "",
    },
  });


  // Load invoices
  const fetchInvoices = async () => {

    try {

      const response = await api.get("/invoices");

      setInvoices(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load invoices"
      );

    }
  };


  // Load sales orders
  const fetchSalesOrders = async () => {

    try {

      const response = await api.get("/sales-orders");

      setSalesOrders(response.data);

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to load sales orders"
      );

    }
  };


  useEffect(() => {

    fetchInvoices();
    fetchSalesOrders();

  }, []);


  // Open dialog
  const handleAdd = () => {

    reset({
      salesOrderId: "",
    });

    setOpenDialog(true);
  };


  // Close dialog
  const handleClose = () => {

    setOpenDialog(false);

    reset({
      salesOrderId: "",
    });
  };


  // Create invoice
  const onSubmit = async (data) => {

    try {

      await api.post("/invoices", {
        salesOrderId: Number(data.salesOrderId),
      });

      setMessage("Invoice created successfully");

      handleClose();

      fetchInvoices();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to create invoice"
      );

    }
  };


  // Update invoice status
  const handleStatusChange = async (
    invoiceId,
    status
  ) => {

    try {

      await api.put(
        `/invoices/${invoiceId}/status`,
        null,
        {
          params: {
            status: status,
          },
        }
      );

      setMessage("Invoice status updated successfully");

      fetchInvoices();

    } catch (err) {

      setError(
        err.response?.data?.message ||
        "Failed to update invoice status"
      );

    }
  };


  // Download invoice PDF
  const handleDownloadPdf = async (invoice) => {

    try {

      const response = await api.get(
        `/invoices/${invoice.id}/pdf`,
        {
          responseType: "blob",
        }
      );

      const blob = new Blob(
        [response.data],
        {
          type: "application/pdf",
        }
      );

      const url = window.URL.createObjectURL(blob);

      const link = document.createElement("a");

      link.href = url;

      link.download =
        `${invoice.invoiceNumber}.pdf`;

      document.body.appendChild(link);

      link.click();

      link.remove();

      window.URL.revokeObjectURL(url);

      setMessage("Invoice PDF downloaded successfully");

    } catch (err) {

      setError(
        "Failed to download invoice PDF"
      );

    }
  };


  const getCustomerName = (customer) => {

    if (!customer) {
      return "Unknown";
    }

    return customer.customerName || "Unknown";
  };


  const getProductName = (product) => {

    if (!product) {
      return "Unknown";
    }

    return product.productName || "Unknown";
  };


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
            Invoices
          </Typography>

          <Typography
            variant="body2"
            color="text.secondary"
          >
            Manage invoices and generate PDF documents
          </Typography>

        </Box>


        <Button
          variant="contained"
          startIcon={<Add />}
          onClick={handleAdd}
        >
          Create Invoice
        </Button>

      </Box>


      {/* Invoice Table */}

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
                <strong>Invoice Number</strong>
              </TableCell>

              <TableCell>
                <strong>Customer</strong>
              </TableCell>

              <TableCell>
                <strong>Sales Order</strong>
              </TableCell>

              <TableCell>
                <strong>Invoice Date</strong>
              </TableCell>

              <TableCell>
                <strong>Total Amount</strong>
              </TableCell>

              <TableCell>
                <strong>Status</strong>
              </TableCell>

              <TableCell align="center">
                <strong>PDF</strong>
              </TableCell>

            </TableRow>

          </TableHead>


          <TableBody>

            {invoices.length === 0 ? (

              <TableRow>

                <TableCell
                  colSpan={8}
                  align="center"
                >
                  No invoices found
                </TableCell>

              </TableRow>

            ) : (

              invoices.map((invoice) => (

                <TableRow
                  key={invoice.id}
                  hover
                >

                  <TableCell>
                    {invoice.id}
                  </TableCell>


                  <TableCell>
                    {invoice.invoiceNumber}
                  </TableCell>


                  <TableCell>
                    {getCustomerName(
                      invoice.customer
                    )}
                  </TableCell>


                  <TableCell>
                    {invoice.salesOrder?.id
                      ? `SO-${invoice.salesOrder.id}`
                      : "Unknown"}
                  </TableCell>


                  <TableCell>
                    {invoice.invoiceDate}
                  </TableCell>


                  <TableCell>
                    {formatAmount(
                      invoice.totalAmount
                    )}
                  </TableCell>


                  <TableCell>

                    <FormControl
                      size="small"
                      sx={{ minWidth: 140 }}
                    >

                      <Select
                        value={invoice.status || ""}
                        onChange={(event) =>
                          handleStatusChange(
                            invoice.id,
                            event.target.value
                          )
                        }
                      >

                        <MenuItem value="PENDING">
                          PENDING
                        </MenuItem>

                        <MenuItem value="PAID">
                          PAID
                        </MenuItem>

                        <MenuItem value="CANCELLED">
                          CANCELLED
                        </MenuItem>

                      </Select>

                    </FormControl>

                  </TableCell>


                  <TableCell align="center">

                    <IconButton
                      color="primary"
                      onClick={() =>
                        handleDownloadPdf(invoice)
                      }
                      title="Download PDF"
                    >

                      <Download />

                    </IconButton>

                  </TableCell>

                </TableRow>

              ))

            )}

          </TableBody>

        </Table>

      </TableContainer>


      {/* Create Invoice Dialog */}

      <Dialog
        open={openDialog}
        onClose={handleClose}
        fullWidth
        maxWidth="sm"
      >

        <DialogTitle>
          Create Invoice
        </DialogTitle>


        <DialogContent>

          <Box
            component="form"
            id="invoice-form"
            onSubmit={handleSubmit(onSubmit)}
            sx={{
              display: "flex",
              flexDirection: "column",
              gap: 2,
              mt: 1,
            }}
          >

            <FormControl
              fullWidth
              error={!!errors.salesOrderId}
            >

              <InputLabel>
                Sales Order
              </InputLabel>

              <Select
                label="Sales Order"
                defaultValue=""
                {...register("salesOrderId")}
              >

                <MenuItem value="">
                  <em>Select Sales Order</em>
                </MenuItem>


                {salesOrders.map((order) => (

                  <MenuItem
                    key={order.id}
                    value={order.id}
                  >

                    {`SO-${order.id}`}
                    {" | "}
                    {getCustomerName(order.customer)}
                    {" | "}
                    {getProductName(order.product)}
                    {" | ₹"}
                    {formatAmount(order.totalAmount).replace(
                      "₹",
                      ""
                    )}

                  </MenuItem>

                ))}

              </Select>


              {errors.salesOrderId && (

                <Typography
                  variant="caption"
                  color="error"
                  sx={{ mt: 0.5 }}
                >
                  {errors.salesOrderId.message}
                </Typography>

              )}

            </FormControl>

          </Box>

        </DialogContent>


        <DialogActions>

          <Button onClick={handleClose}>
            Cancel
          </Button>


          <Button
            type="submit"
            form="invoice-form"
            variant="contained"
          >
            Create Invoice
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


export default Invoices;