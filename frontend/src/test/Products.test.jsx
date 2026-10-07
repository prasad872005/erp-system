import { render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "../context/AuthContext";
import Products from "../pages/Products";
import api from "../services/api";

vi.mock("../services/api", () => ({
    default: {
        get: vi.fn(),
        post: vi.fn(),
        put: vi.fn(),
        delete: vi.fn(),
    },
}));

describe("Products Page", () => {

    test("renders products page after loading products", async () => {

        api.get.mockResolvedValue({
            data: []
        });

        render(
            <MemoryRouter>
                <AuthProvider>
                    <Products />
                </AuthProvider>
            </MemoryRouter>
        );

        await waitFor(() => {
            expect(
                screen.getByRole("heading", { name: "Products" })
            ).toBeInTheDocument();
        });

        expect(
            screen.getByRole("button", { name: /add product/i })
        ).toBeInTheDocument();

        expect(
            screen.getByText("No products found.")
        ).toBeInTheDocument();

        expect(api.get).toHaveBeenCalledWith("/products");
    });

});