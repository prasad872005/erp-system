import { render, screen } from "@testing-library/react";
import { MemoryRouter } from "react-router-dom";
import { AuthProvider } from "../context/AuthContext";
import Login from "../pages/Login";

describe("Login Page", () => {

    test("renders login page correctly", () => {

        render(
            <MemoryRouter>
                <AuthProvider>
                    <Login />
                </AuthProvider>
            </MemoryRouter>
        );

        expect(
            screen.getByText("ERP System")
        ).toBeInTheDocument();

        expect(
            screen.getByPlaceholderText("Username")
        ).toBeInTheDocument();

        expect(
            screen.getByPlaceholderText("Password")
        ).toBeInTheDocument();

        expect(
            screen.getByRole("button", { name: "Login" })
        ).toBeInTheDocument();
    });

});