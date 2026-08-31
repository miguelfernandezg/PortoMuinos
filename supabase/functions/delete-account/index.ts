/*
 * Responsabilidad: elimina de forma segura la cuenta del usuario autenticado
 * mediante el cliente administrativo disponible únicamente en la Edge Function.
 */

import "jsr:@supabase/functions-js/edge-runtime.d.ts";
import { withSupabase } from "jsr:@supabase/server@1";

export default {
  fetch: withSupabase(
    { auth: "user" },

    async (_req, ctx) => {
      try {
        // El identificador procede de la sesión validada por Supabase.
        const userId = ctx.userClaims?.id;

        if (!userId) {
          return Response.json(
            {
              error: "Usuario no autenticado.",
            },
            {
              status: 401,
            },
          );
        }

        // La operación administrativa permanece fuera de la aplicación Android.
        const { error } =
          await ctx.supabaseAdmin.auth.admin.deleteUser(
            userId,
          );

        if (error) {
          console.error(
            "Error deleting user:",
            error,
          );

          return Response.json(
            {
              error:
                "No se pudo eliminar la cuenta.",
            },
            {
              status: 500,
            },
          );
        }

        return Response.json(
          {
            success: true,
          },
          {
            status: 200,
          },
        );
      } catch (error) {
        console.error(
          "Unexpected delete-account error:",
          error,
        );

        return Response.json(
          {
            error:
              "Ha ocurrido un error inesperado.",
          },
          {
            status: 500,
          },
        );
      }
    },
  ),
};
