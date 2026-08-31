-- ============================================================
-- PORTO-MUIÑOS TFG
-- Esquema Supabase reproducible
-- Estado recomendado/final tras eliminar políticas duplicadas.
--
-- Incluye:
--   - products
--   - recipes
--   - recipe_products
--   - favorite_products
--   - favorite_recipes
--   - cart_items
--   - índices
--   - RLS
--   - bucket catalog-media
-- ============================================================

-- ------------------------------------------------------------
-- TABLAS DE CATÁLOGO
-- ------------------------------------------------------------

create table if not exists public.products (
    id text primary key,
    name text not null,
    description text not null,
    price numeric(10, 2) not null,
    image_url text,
    qr_code text unique,
    created_at timestamptz not null default now(),
    subtitle text not null default '',
    category text not null default 'ALGAS',
    sku text,
    source_url text
);

create unique index if not exists products_sku_unique
on public.products (sku)
where sku is not null;


create table if not exists public.recipes (
    id text primary key,
    name text not null,
    description text,
    duration_minutes integer not null,
    difficulty text not null,
    category text not null,
    ingredients jsonb not null default '[]'::jsonb,
    steps jsonb not null default '[]'::jsonb,
    image_url text,
    created_at timestamptz not null default now(),
    source_url text
);


create table if not exists public.recipe_products (
    recipe_id text not null
        references public.recipes(id)
        on delete cascade,

    product_id text not null
        references public.products(id)
        on delete cascade,

    primary key (
        recipe_id,
        product_id
    )
);


-- ------------------------------------------------------------
-- FAVORITOS POR USUARIO
-- ------------------------------------------------------------

create table if not exists public.favorite_products (
    user_id uuid not null
        references auth.users(id)
        on delete cascade,

    product_id text not null
        references public.products(id)
        on delete cascade,

    created_at timestamptz not null
        default now(),

    primary key (
        user_id,
        product_id
    )
);


create table if not exists public.favorite_recipes (
    user_id uuid not null
        references auth.users(id)
        on delete cascade,

    recipe_id text not null
        references public.recipes(id)
        on delete cascade,

    created_at timestamptz not null
        default now(),

    primary key (
        user_id,
        recipe_id
    )
);


-- ------------------------------------------------------------
-- CARRITO POR USUARIO
-- ------------------------------------------------------------

create table if not exists public.cart_items (
    user_id uuid not null
        references auth.users(id)
        on delete cascade,

    product_id text not null
        references public.products(id)
        on delete cascade,

    quantity integer not null
        default 1
        check (quantity > 0),

    created_at timestamptz not null
        default now(),

    primary key (
        user_id,
        product_id
    )
);


-- ------------------------------------------------------------
-- ROW LEVEL SECURITY
-- ------------------------------------------------------------

alter table public.products
enable row level security;

alter table public.recipes
enable row level security;

alter table public.recipe_products
enable row level security;

alter table public.favorite_products
enable row level security;

alter table public.favorite_recipes
enable row level security;

alter table public.cart_items
enable row level security;


-- Catálogo: lectura pública.

drop policy if exists
"Products are publicly readable"
on public.products;

create policy
"Products are publicly readable"
on public.products
for select
to public
using (true);


drop policy if exists
"Recipes are publicly readable"
on public.recipes;

create policy
"Recipes are publicly readable"
on public.recipes
for select
to public
using (true);


drop policy if exists
"Recipe products are publicly readable"
on public.recipe_products;

create policy
"Recipe products are publicly readable"
on public.recipe_products
for select
to public
using (true);


-- Favoritos de productos: cada usuario solo accede a los suyos.

drop policy if exists
"Users can view their own favorite products"
on public.favorite_products;

create policy
"Users can view their own favorite products"
on public.favorite_products
for select
to authenticated
using (
    (select auth.uid()) = user_id
);


drop policy if exists
"Users can insert their own favorite products"
on public.favorite_products;

create policy
"Users can insert their own favorite products"
on public.favorite_products
for insert
to authenticated
with check (
    (select auth.uid()) = user_id
);


drop policy if exists
"Users can delete their own favorite products"
on public.favorite_products;

create policy
"Users can delete their own favorite products"
on public.favorite_products
for delete
to authenticated
using (
    (select auth.uid()) = user_id
);


-- Favoritos de recetas.

drop policy if exists
"Users can view their own favorite recipes"
on public.favorite_recipes;

create policy
"Users can view their own favorite recipes"
on public.favorite_recipes
for select
to authenticated
using (
    (select auth.uid()) = user_id
);


drop policy if exists
"Users can insert their own favorite recipes"
on public.favorite_recipes;

create policy
"Users can insert their own favorite recipes"
on public.favorite_recipes
for insert
to authenticated
with check (
    (select auth.uid()) = user_id
);


drop policy if exists
"Users can delete their own favorite recipes"
on public.favorite_recipes;

create policy
"Users can delete their own favorite recipes"
on public.favorite_recipes
for delete
to authenticated
using (
    (select auth.uid()) = user_id
);


-- Carrito.

drop policy if exists
"Users can view their own cart items"
on public.cart_items;

create policy
"Users can view their own cart items"
on public.cart_items
for select
to authenticated
using (
    (select auth.uid()) = user_id
);


drop policy if exists
"Users can insert their own cart items"
on public.cart_items;

create policy
"Users can insert their own cart items"
on public.cart_items
for insert
to authenticated
with check (
    (select auth.uid()) = user_id
);


drop policy if exists
"Users can update their own cart items"
on public.cart_items;

create policy
"Users can update their own cart items"
on public.cart_items
for update
to authenticated
using (
    (select auth.uid()) = user_id
)
with check (
    (select auth.uid()) = user_id
);


drop policy if exists
"Users can delete their own cart items"
on public.cart_items;

create policy
"Users can delete their own cart items"
on public.cart_items
for delete
to authenticated
using (
    (select auth.uid()) = user_id
);


-- Permisos de las tablas privadas.
grant select, insert, delete
on public.favorite_products
to authenticated;

grant select, insert, delete
on public.favorite_recipes
to authenticated;

grant select, insert, update, delete
on public.cart_items
to authenticated;


-- ------------------------------------------------------------
-- STORAGE
-- ------------------------------------------------------------
-- Bucket público usado para imágenes del catálogo:
--   id/name: catalog-media
--   límite: 5 MiB
--   tipos: JPEG, PNG y WebP
--
-- Esta inserción permite recrearlo desde SQL si no existe.
-- No se definieron políticas personalizadas sobre storage.objects
-- en el proyecto auditado.

insert into storage.buckets (
    id,
    name,
    public,
    file_size_limit,
    allowed_mime_types
)
values (
    'catalog-media',
    'catalog-media',
    true,
    5242880,
    array[
        'image/jpeg',
        'image/png',
        'image/webp'
    ]
)
on conflict (id) do update
set
    name = excluded.name,
    public = excluded.public,
    file_size_limit = excluded.file_size_limit,
    allowed_mime_types = excluded.allowed_mime_types;
