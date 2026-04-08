-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 01, 2026 at 09:49 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `greenly_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `batches`
--

CREATE TABLE `batches` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `supplier_id` bigint(20) UNSIGNED NOT NULL,
  `batch_code` varchar(50) NOT NULL COMMENT 'Mã lô hàng',
  `import_price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `quantity` int(11) NOT NULL DEFAULT 0 COMMENT 'Số lượng nhập ban đầu',
  `current_quantity` int(11) NOT NULL DEFAULT 0 COMMENT 'Số lượng còn lại trong kho',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `batches`
--

INSERT INTO `batches` (`id`, `product_id`, `supplier_id`, `batch_code`, `import_price`, `quantity`, `current_quantity`, `created_at`, `updated_at`) VALUES
(4, 17, 8, 'LOT-2026-001', 9000.00, 200, 196, '2026-03-18 03:00:00', '2026-04-01 10:07:01'),
(5, 18, 3, 'LOT-2026-002', 22000.00, 100, 99, '2026-03-18 03:05:00', '2026-04-01 10:07:01'),
(6, 19, 8, 'LOT-2026-003', 30000.00, 80, 80, '2026-03-18 03:10:00', '2026-03-17 20:03:42'),
(7, 20, 9, 'LOT-2026-004', 18000.00, 150, 149, '2026-03-18 03:15:00', '2026-03-29 13:16:25'),
(8, 21, 3, 'LOT-2026-005', 15000.00, 120, 120, '2026-03-18 03:20:00', '2026-03-17 20:03:19'),
(9, 22, 7, 'LOT-2026-006', 70000.00, 50, 50, '2026-03-18 03:25:00', '2026-03-17 20:00:32'),
(10, 23, 9, 'LOT-2026-007', 25000.00, 200, 199, '2026-03-18 03:30:00', '2026-03-31 20:16:56'),
(11, 24, 7, 'LOT-2026-008', 40000.00, 120, 117, '2026-03-18 03:35:00', '2026-04-01 12:19:32'),
(12, 25, 7, 'LOT-2026-009', 30000.00, 150, 148, '2026-03-18 03:40:00', '2026-04-01 10:27:24'),
(13, 26, 7, 'LOT-2026-010', 110000.00, 40, 39, '2026-03-18 03:45:00', '2026-04-01 12:19:32'),
(14, 27, 6, 'LOT-2026-011', 120000.00, 60, 59, '2026-03-18 03:50:00', '2026-03-31 23:51:35'),
(15, 28, 6, 'LOT-2026-012', 90000.00, 80, 78, '2026-03-18 03:55:00', '2026-04-01 10:27:24'),
(16, 29, 6, 'LOT-2026-013', 260000.00, 30, 29, '2026-03-18 04:00:00', '2026-03-31 22:22:35'),
(17, 31, 9, 'LOT-2026-014', 160000.00, 20, 0, '2026-03-18 04:05:00', '2026-03-24 06:34:25'),
(18, 33, 10, 'LOT-2026-015', 250000.00, 40, 39, '2026-03-18 04:10:00', '2026-03-31 19:32:33'),
(19, 32, 7, 'LOT-2026-016', 420000.00, 25, 22, '2026-03-18 04:15:00', '2026-04-01 11:03:32'),
(20, 34, 5, 'LOT-2026-017', 150000.00, 60, 60, '2026-03-18 04:20:00', '2026-03-17 19:58:23'),
(21, 35, 10, 'LOT-2026-018', 200000.00, 50, 49, '2026-03-18 04:25:00', '2026-03-31 18:31:13'),
(22, 36, 5, 'LOT-2026-019', 200000.00, 30, 30, '2026-03-18 04:30:00', '2026-03-17 20:02:47'),
(23, 30, 8, 'LOT-2026-020', 130000.00, 70, 70, '2026-03-17 20:08:09', '2026-03-17 20:08:09');

-- --------------------------------------------------------

--
-- Table structure for table `cache`
--

CREATE TABLE `cache` (
  `key` varchar(255) NOT NULL,
  `value` mediumtext NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `cache_locks`
--

CREATE TABLE `cache_locks` (
  `key` varchar(255) NOT NULL,
  `owner` varchar(255) NOT NULL,
  `expiration` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `carts`
--

CREATE TABLE `carts` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `categories`
--

CREATE TABLE `categories` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(100) NOT NULL,
  `image` varchar(255) DEFAULT NULL,
  `description` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `categories`
--

INSERT INTO `categories` (`id`, `name`, `image`, `description`, `created_at`, `updated_at`) VALUES
(19, 'Rau củ tươi', 'storage/categories/OaRipbWfgXlyM95izynbZX2coIkjabCfJ3EvDHYi.png', 'Rau xanh và củ quả thu hoạch tươi, không dùng thuốc bảo vệ thực vật hoặc theo tiêu chuẩn hữu cơ.', '2026-03-17 09:41:51', '2026-03-23 18:49:39'),
(20, 'Trái cây', 'storage/categories/Xatgdt23RrQEqmpE6hwd4WLETc4JzbCo2ctFJjbm.png', 'Trái cây tươi theo mùa và nhập khẩu, kiểm soát dư lượng thuốc, đóng gói an toàn.', '2026-03-17 09:45:49', '2026-03-23 18:49:27'),
(21, 'Thịt tươi', 'storage/categories/gYlL3bqfabcOwuqT6bxoWppIp7VAKy6Y9DAipoaF.png', 'Thịt bò, heo, gà từ trang trại kiểm soát chất lượng, không sử dụng kháng sinh tăng trưởng.', '2026-03-17 15:58:00', '2026-03-23 18:43:15'),
(22, 'Hải sản', 'storage/categories/NMgMkEW1YQksMBUNrIBRQlZhEQRMYjOBsUPZJA4p.png', 'Cá, tôm, mực tươi hoặc cấp đông nhanh, nguồn gốc rõ ràng, kiểm dịch đầy đủ.', '2026-03-17 15:58:00', '2026-03-23 18:43:52'),
(23, 'Sản phẩm hữu cơ', 'storage/categories/LbUhKSvKcLs52vk9tvh9tnNml7bl5PZtFX5GVo2c.png', 'Thực phẩm chứng nhận hữu cơ (organic) theo tiêu chuẩn quốc tế hoặc địa phương.', '2026-03-17 15:58:00', '2026-03-23 18:44:17'),
(24, 'Sữa và chế phẩm', 'storage/categories/j7Gz1EaITZ8TWn0tExUvx1Lo0WoImal92lUC8uko.png', 'Sữa tươi, sữa chua, phô mai từ nguồn an toàn, ít hoặc không chất bảo quản.', '2026-03-17 15:58:00', '2026-03-23 18:44:52'),
(25, 'Trứng và gia cầm', 'storage/categories/SDWvMsj3pnF6lBRy4352vWmDEq6GfjIjwJawlxTf.png', 'Trứng sạch, gia cầm nuôi thả vườn hoặc theo tiêu chuẩn an toàn thực phẩm.', '2026-03-17 15:58:00', '2026-03-23 18:45:16'),
(26, 'Ngũ cốc và đậu', 'storage/categories/ZrfLyif9eRDUQmRxyGbUTV5MrZw5DmlEYMBKOgUi.png', 'Gạo, bột, yến mạch, các loại đậu khô và hạt dinh dưỡng, không tạp chất.', '2026-03-17 15:58:00', '2026-03-23 18:45:32'),
(27, 'Thực phẩm chế biến nhẹ', 'storage/categories/b0COFzCyPBkQPEXFlaUtsp75FdqKZYVLCilGuW5s.png', 'Sản phẩm chế biến sẵn lành mạnh: salad, đồ ăn sẵn từ nguyên liệu sạch.', '2026-03-17 15:58:00', '2026-03-23 18:45:57'),
(28, 'Thực phẩm đông lạnh', 'storage/categories/sykofM4DmtsAkvG3GwKrfxuqyazAGRmcAcaOcWe0.png', 'Sản phẩm cấp đông nhanh giữ dinh dưỡng: rau, thịt, hải sản, món ăn chế biến.', '2026-03-17 15:58:00', '2026-03-23 18:46:23'),
(29, 'Gia vị và nước chấm', 'storage/categories/c2dlGRhAxyepyWLX8sPCnCiNvcRg5dhF2fpVtlJF.png', 'Gia vị, nước mắm, dầu ăn, tương ớt, muối tiêu chọn lọc, không chất tạo mùi.', '2026-03-17 15:58:00', '2026-03-23 18:46:57'),
(30, 'Đồ uống tự nhiên', 'storage/categories/y3OsiabwBCbmP5WW46Sze1BVMhGoECy91X1h6nX9.png', 'Nước ép, trà thảo mộc, sữa hạt, nước uống không đường phụ gia.', '2026-03-17 15:58:00', '2026-03-23 18:47:08'),
(31, 'Đồ ăn vặt lành mạnh', 'storage/categories/mNWeIkPFOEbg8APnE6esZLmYJeo6beQqc5OZUGDB.png', 'Snack từ nguyên liệu tự nhiên: hạt, trái sấy, thanh dinh dưỡng ít đường.', '2026-03-17 15:58:00', '2026-03-23 18:47:24'),
(32, 'Sản phẩm cho bé', 'storage/categories/vkpR86QAcK1n7EMKrB0OocOSm67WI1wZ4Weq7nBf.png', 'Thực phẩm, bột ăn dặm, đồ uống dành riêng cho trẻ em, an toàn và dinh dưỡng.', '2026-03-17 15:58:00', '2026-03-23 18:47:45'),
(33, 'Sản phẩm theo chế độ ăn', 'storage/categories/3swKzrLHe29NAx31pG2Qls3k0XAYScUf7v8XmQMD.png', 'Thực phẩm cho người ăn kiêng, ăn chay, không gluten, low-carb, keto.', '2026-03-17 15:58:00', '2026-03-23 18:48:10'),
(34, 'Dụng cụ & bao bì thân thiện', 'storage/categories/5h3cEQIknj62gYB8Kqa5E3vsF06lHKqzFHw6jnJ0.png', 'Túi, hộp, dụng cụ bếp thân thiện môi trường, bao bì tái chế hoặc phân hủy.', '2026-03-17 15:58:00', '2026-03-23 18:48:26'),
(35, 'Combo/Set theo bữa', 'storage/categories/LR818m2nVrq2ZI0LHjcSVnpjeceBvYxn7jg8D9AY.png', 'Gói nguyên liệu theo bữa (set nấu ăn) tiện lợi, đã cân đối dinh dưỡng và hướng dẫn nấu.', '2026-03-17 15:58:00', '2026-03-23 18:48:48'),
(36, 'Hộp đăng ký định kỳ', 'storage/categories/KBApPYsXgitEosQjdAQMTAnAwumOvkzeDk3DFWBM.png', 'Hộp rau quả hoặc thực phẩm giao hàng định kỳ theo tuần/tháng, tùy chọn gói và lịch giao.', '2026-03-17 15:58:00', '2026-03-23 18:49:14');

-- --------------------------------------------------------

--
-- Table structure for table `failed_jobs`
--

CREATE TABLE `failed_jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `connection` text NOT NULL,
  `queue` text NOT NULL,
  `payload` longtext NOT NULL,
  `exception` longtext NOT NULL,
  `failed_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `jobs`
--

CREATE TABLE `jobs` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `queue` varchar(255) NOT NULL,
  `payload` longtext NOT NULL,
  `attempts` tinyint(3) UNSIGNED NOT NULL,
  `reserved_at` int(10) UNSIGNED DEFAULT NULL,
  `available_at` int(10) UNSIGNED NOT NULL,
  `created_at` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `job_batches`
--

CREATE TABLE `job_batches` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `total_jobs` int(11) NOT NULL,
  `pending_jobs` int(11) NOT NULL,
  `failed_jobs` int(11) NOT NULL,
  `failed_job_ids` longtext NOT NULL,
  `options` mediumtext DEFAULT NULL,
  `cancelled_at` int(11) DEFAULT NULL,
  `created_at` int(11) NOT NULL,
  `finished_at` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `migrations`
--

CREATE TABLE `migrations` (
  `id` int(10) UNSIGNED NOT NULL,
  `migration` varchar(255) NOT NULL,
  `batch` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `migrations`
--

INSERT INTO `migrations` (`id`, `migration`, `batch`) VALUES
(1, '0001_01_01_000000_create_users_table', 1),
(2, '0001_01_01_000001_create_cache_table', 1),
(3, '0001_01_01_000002_create_jobs_table', 1),
(4, '2026_03_02_075559_create_categories_table', 2),
(5, '2026_03_02_075608_create_suppliers_table', 2),
(6, '2026_03_02_075616_create_products_table', 2),
(7, '2026_03_02_075623_create_batches_table', 2),
(8, '2026_03_02_075633_create_carts_table', 2),
(9, '2026_03_02_075640_create_orders_table', 2),
(10, '2026_03_02_075648_create_order_details_table', 2),
(11, '2026_03_02_075654_create_reviews_table', 2),
(12, '2026_03_12_170652_add_is_active_to_suppliers_table', 3),
(13, '2026_03_13_071734_drop_dates_from_batches_table', 4),
(14, '2026_03_14_054955_update_orders_table_add_status_and_shipping_fields', 5),
(15, '2026_03_14_084557_add_status_to_users_table', 6),
(16, '2026_03_15_101904_add_status_and_reply_to_reviews_table', 7),
(17, '2026_03_15_182053_add_work_status_to_users_table', 8),
(18, '2026_03_15_183254_add_shipper_rating_to_orders_table', 9),
(19, '2026_03_24_041311_create_personal_access_tokens_table', 10),
(20, '2026_04_01_022443_update_order_status_enum_in_orders_table', 11);

-- --------------------------------------------------------

--
-- Table structure for table `orders`
--

CREATE TABLE `orders` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `shipper_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT 'Mã nhân viên giao hàng',
  `total_money` decimal(12,2) NOT NULL DEFAULT 0.00,
  `shipping_fee` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Phí vận chuyển',
  `payment_method` varchar(50) NOT NULL DEFAULT 'COD',
  `payment_status` enum('pending','completed','failed') NOT NULL DEFAULT 'pending',
  `payment_receipt` varchar(255) DEFAULT NULL COMMENT 'Đường dẫn file ảnh hóa đơn',
  `order_status` enum('pending','processing','ready_for_pickup','shipping','delivered','cancelled') NOT NULL DEFAULT 'pending',
  `shipping_address` text NOT NULL,
  `shipping_name` varchar(100) DEFAULT NULL,
  `shipping_phone` varchar(20) DEFAULT NULL,
  `note` text DEFAULT NULL,
  `delivery_date` datetime DEFAULT NULL COMMENT 'Thời gian giao hàng thành công',
  `shipper_rating` tinyint(3) UNSIGNED DEFAULT NULL COMMENT 'Đánh giá shipper: 1-5 sao',
  `shipper_review` text DEFAULT NULL COMMENT 'Nhận xét của khách về shipper',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `orders`
--

INSERT INTO `orders` (`id`, `user_id`, `shipper_id`, `total_money`, `shipping_fee`, `payment_method`, `payment_status`, `payment_receipt`, `order_status`, `shipping_address`, `shipping_name`, `shipping_phone`, `note`, `delivery_date`, `shipper_rating`, `shipper_review`, `created_at`, `updated_at`) VALUES
(3, 8, 5, 730000.00, 20000.00, 'COD', 'pending', NULL, 'delivered', 'Ký túc xã tầng 10, 141 đường chiến thắng, Tân triều, Thanh trì, Hà Nội', 'Nguyễn Văn B', '0896720844', NULL, '2026-04-01 02:42:50', NULL, NULL, '2026-03-31 18:31:13', '2026-03-31 19:42:50'),
(4, 8, 5, 360000.00, 20000.00, 'COD', 'pending', NULL, 'cancelled', 'Ký túc xã tầng 10, 141 chiến thắng, Tân triều, Thanh Trì, Hà Nội', 'Nguyễn Văn B', '0896720844', NULL, NULL, NULL, NULL, '2026-03-31 19:32:33', '2026-03-31 20:14:06'),
(7, 8, 5, 280000.00, 20000.00, 'COD', 'pending', NULL, 'cancelled', 'Ký túc xã tầng 10, 14 chiến thắng, Tân triều, Thanh trì, Hà Nội', 'Nguyễn Văn B', '0896720844', NULL, NULL, NULL, NULL, '2026-03-31 23:51:35', '2026-04-01 10:04:08'),
(8, 8, 5, 95000.00, 20000.00, 'COD', 'pending', NULL, 'cancelled', 'Ký túc xã tầng 10, 141 đường chiến thắng Học viện kỹ thuật mật mã, Tân triều, Thanh trì, Hà Nội', 'Nguyễn Văn B', '0896720844', NULL, NULL, NULL, NULL, '2026-04-01 10:07:01', '2026-04-01 10:25:46'),
(9, 8, 5, 237000.00, 20000.00, 'COD', 'pending', NULL, 'delivered', 'Ký túc xã tầng 10, 141 đường chiến thắng Học viện Kỹ thuật mật mã, Tân triều, Thanh trì, Hà Nội', 'Nguyễn Văn B', '0896720844', NULL, '2026-04-01 18:00:50', NULL, NULL, '2026-04-01 10:27:24', '2026-04-01 11:00:50'),
(10, 8, 5, 500000.00, 20000.00, 'COD', 'pending', NULL, 'delivered', 'Ký túc xã tầng 10, 141 chiến thắng, tân triều, Thanh trì, Hà Nội', 'Nguyễn Văn B', '0896720844', NULL, '2026-04-01 18:26:30', NULL, NULL, '2026-04-01 11:03:32', '2026-04-01 11:26:30'),
(11, 8, NULL, 204000.00, 20000.00, 'COD', 'pending', NULL, 'processing', 'Ký túc xã tầng 10, 141 chiến thắng, Tân triều, Thanh trì, Hà Nội', 'Nguyễn Văn B', '0896720844', NULL, NULL, NULL, NULL, '2026-04-01 12:19:32', '2026-04-01 12:19:41');

-- --------------------------------------------------------

--
-- Table structure for table `order_details`
--

CREATE TABLE `order_details` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `order_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `batch_id` bigint(20) UNSIGNED DEFAULT NULL COMMENT 'Mã lô hàng xuất ra',
  `quantity` int(11) NOT NULL DEFAULT 1,
  `price` decimal(10,2) NOT NULL COMMENT 'Giá tại thời điểm mua',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `order_details`
--

INSERT INTO `order_details` (`id`, `order_id`, `product_id`, `batch_id`, `quantity`, `price`, `created_at`, `updated_at`) VALUES
(4, 3, 32, 19, 1, 480000.00, '2026-03-31 18:31:13', '2026-03-31 18:31:13'),
(5, 3, 35, 21, 1, 230000.00, '2026-03-31 18:31:13', '2026-03-31 18:31:13'),
(6, 4, 33, 18, 1, 340000.00, '2026-03-31 19:32:33', '2026-03-31 19:32:33'),
(12, 7, 28, 15, 1, 120000.00, '2026-03-31 23:51:35', '2026-03-31 23:51:35'),
(13, 7, 27, 14, 1, 140000.00, '2026-03-31 23:51:35', '2026-03-31 23:51:35'),
(14, 8, 17, 4, 2, 15000.00, '2026-04-01 10:07:01', '2026-04-01 10:07:01'),
(15, 8, 18, 5, 1, 45000.00, '2026-04-01 10:07:01', '2026-04-01 10:07:01'),
(16, 9, 28, 15, 1, 120000.00, '2026-04-01 10:27:24', '2026-04-01 10:27:24'),
(17, 9, 25, 12, 1, 42000.00, '2026-04-01 10:27:24', '2026-04-01 10:27:24'),
(18, 9, 24, 11, 1, 55000.00, '2026-04-01 10:27:24', '2026-04-01 10:27:24'),
(19, 10, 32, 19, 1, 480000.00, '2026-04-01 11:03:32', '2026-04-01 11:03:32'),
(20, 11, 26, 13, 1, 129000.00, '2026-04-01 12:19:32', '2026-04-01 12:19:32'),
(21, 11, 24, 11, 1, 55000.00, '2026-04-01 12:19:32', '2026-04-01 12:19:32');

-- --------------------------------------------------------

--
-- Table structure for table `password_reset_tokens`
--

CREATE TABLE `password_reset_tokens` (
  `email` varchar(255) NOT NULL,
  `token` varchar(255) NOT NULL,
  `created_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `personal_access_tokens`
--

CREATE TABLE `personal_access_tokens` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `tokenable_type` varchar(255) NOT NULL,
  `tokenable_id` bigint(20) UNSIGNED NOT NULL,
  `name` text NOT NULL,
  `token` varchar(64) NOT NULL,
  `abilities` text DEFAULT NULL,
  `last_used_at` timestamp NULL DEFAULT NULL,
  `expires_at` timestamp NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `personal_access_tokens`
--

INSERT INTO `personal_access_tokens` (`id`, `tokenable_type`, `tokenable_id`, `name`, `token`, `abilities`, `last_used_at`, `expires_at`, `created_at`, `updated_at`) VALUES
(1, 'App\\Models\\User', 8, 'auth_token', '8336db6c0c814842364263cf77e0cccb01d50d7350feedfb927a1b486675bd88', '[\"*\"]', NULL, NULL, '2026-03-26 11:03:46', '2026-03-26 11:03:46'),
(2, 'App\\Models\\User', 8, 'auth_token', '098b73d4b396625fea959bdb9f4442a87613a1b981b4a9097e6cb0a02171ee6e', '[\"*\"]', NULL, NULL, '2026-03-26 11:07:15', '2026-03-26 11:07:15'),
(3, 'App\\Models\\User', 8, 'auth_token', 'd6c51cd996720b66b9ed8798ed659ad8ce3acba871bf92c2d254de94acac7389', '[\"*\"]', NULL, NULL, '2026-03-27 01:40:05', '2026-03-27 01:40:05'),
(4, 'App\\Models\\User', 8, 'auth_token', '44f0454bd6cb8c7f18447611f7ec070927bfdfcccf0c18990966e101457dbec4', '[\"*\"]', NULL, NULL, '2026-03-27 03:26:33', '2026-03-27 03:26:33'),
(5, 'App\\Models\\User', 8, 'auth_token', '91652ba00fdbf16d2ed1f04ee571f1bc896bad5eeaa34bd2232d88465ab5ebef', '[\"*\"]', NULL, NULL, '2026-03-27 03:47:57', '2026-03-27 03:47:57'),
(6, 'App\\Models\\User', 8, 'auth_token', 'be8ef85f52a82da94b834b9008f64ab0b227f0dc141d905bee23444f022e1535', '[\"*\"]', NULL, NULL, '2026-03-27 04:02:53', '2026-03-27 04:02:53'),
(7, 'App\\Models\\User', 8, 'auth_token', 'f4bcc08f626a764c19b27db1e6f86ee7d5862f5d2dbbff33355299d5f88a2393', '[\"*\"]', NULL, NULL, '2026-03-27 04:06:23', '2026-03-27 04:06:23'),
(8, 'App\\Models\\User', 8, 'auth_token', '0ee4b3caa5999ba04f2cd904a7ef4d45a4e79be48cb67a974cb238f12ca8d55a', '[\"*\"]', NULL, NULL, '2026-03-27 04:27:45', '2026-03-27 04:27:45'),
(9, 'App\\Models\\User', 8, 'auth_token', '4de227300734c4b1b5fd3b795b3a240815f29f95419f0d56e36fc7d741fea79d', '[\"*\"]', NULL, NULL, '2026-03-27 04:37:21', '2026-03-27 04:37:21'),
(10, 'App\\Models\\User', 8, 'auth_token', '7d9f32b2c5d09cd4b495270193d9f4351f20ca26767a3f6f3b003797617baa49', '[\"*\"]', NULL, NULL, '2026-03-27 04:41:33', '2026-03-27 04:41:33'),
(11, 'App\\Models\\User', 8, 'auth_token', 'e52092770ee1048e72c96a91a50e94ad4a4c22757a5c5376507a089b09bdeb8b', '[\"*\"]', NULL, NULL, '2026-03-27 11:26:26', '2026-03-27 11:26:26'),
(12, 'App\\Models\\User', 8, 'auth_token', 'd170faa2ce870a94140c5902585301d26558ddc4d00bd691d721eb318fd23358', '[\"*\"]', NULL, NULL, '2026-03-27 23:23:40', '2026-03-27 23:23:40'),
(13, 'App\\Models\\User', 8, 'auth_token', 'e6227178ba544014e093129be0ea96652b995b79bce9dc527ed9ac102ed23b58', '[\"*\"]', NULL, NULL, '2026-03-27 23:58:16', '2026-03-27 23:58:16'),
(14, 'App\\Models\\User', 8, 'auth_token', '15805c85af09b0dc7fcd7463f6fe5ea52bf61681f19a4549d81cff6f346dff8d', '[\"*\"]', NULL, NULL, '2026-03-28 01:57:24', '2026-03-28 01:57:24'),
(15, 'App\\Models\\User', 8, 'auth_token', 'ec336573597362c814f7e770af807fba611379acc7c9cd0b1e9d17044a764383', '[\"*\"]', NULL, NULL, '2026-03-28 01:59:23', '2026-03-28 01:59:23'),
(16, 'App\\Models\\User', 8, 'auth_token', '4b83dd55c67ffb4c723f7dcb7adcf6d750fb50a6ecca2c41d2c9617f8f95942a', '[\"*\"]', NULL, NULL, '2026-03-28 02:12:07', '2026-03-28 02:12:07'),
(17, 'App\\Models\\User', 8, 'auth_token', '308e848ecd283593b5494a43feefb7d76075f5e4de5b1f8f0dd90dac78f9f3a9', '[\"*\"]', NULL, NULL, '2026-03-28 02:22:34', '2026-03-28 02:22:34'),
(18, 'App\\Models\\User', 8, 'auth_token', '5bf620d45776780b2fe2e5e63330ca92899aaba4d0af600455d98da9ebafd7aa', '[\"*\"]', NULL, NULL, '2026-03-28 03:27:22', '2026-03-28 03:27:22'),
(19, 'App\\Models\\User', 8, 'auth_token', 'edd2a5d65c9bb34e664cf6a8f76eb7375fafa2bae5d6c28a9dbfaac1478fd6a9', '[\"*\"]', '2026-03-28 06:46:04', NULL, '2026-03-28 05:14:06', '2026-03-28 06:46:04'),
(20, 'App\\Models\\User', 8, 'auth_token', '6b3a68056972a944a27847eda905de640cf8f1380600cf7ee2b0a3c28bdd3ba0', '[\"*\"]', '2026-03-28 05:52:12', NULL, '2026-03-28 05:45:13', '2026-03-28 05:52:12'),
(21, 'App\\Models\\User', 8, 'auth_token', '467127d15471809fbe028a4587d51ecffa637bb2d56aed00d485c688b28f853f', '[\"*\"]', '2026-03-28 08:51:10', NULL, '2026-03-28 07:44:32', '2026-03-28 08:51:10'),
(22, 'App\\Models\\User', 8, 'auth_token', 'c1fc2d884081afef0db410785dbfa11028cf7caa48ce3d289be030a52e40b863', '[\"*\"]', '2026-03-28 12:27:53', NULL, '2026-03-28 08:54:43', '2026-03-28 12:27:53'),
(23, 'App\\Models\\User', 8, 'auth_token', '8134f08626e3e1d14e40682080e826638c54e98fe55e145e625759d8877f3c68', '[\"*\"]', '2026-03-28 12:32:04', NULL, '2026-03-28 12:31:39', '2026-03-28 12:32:04'),
(24, 'App\\Models\\User', 8, 'auth_token', 'f9654a0c94a828ca8a45729d4f312eaecf429180c0ae3ac5532fc332c94cde4d', '[\"*\"]', '2026-03-28 12:57:32', NULL, '2026-03-28 12:33:11', '2026-03-28 12:57:32'),
(25, 'App\\Models\\User', 8, 'auth_token', 'd8c22295065aaad80fc950e457a6a905a82382d70f8b8f11b277ff244f026b19', '[\"*\"]', '2026-03-30 18:32:58', NULL, '2026-03-28 13:03:14', '2026-03-30 18:32:58'),
(26, 'App\\Models\\User', 5, 'auth_token', '427beffd41bbad30dc62732581d2e936dc4e24a6f655a1fb5ff083cbf50a4f86', '[\"*\"]', NULL, NULL, '2026-03-31 10:54:20', '2026-03-31 10:54:20'),
(27, 'App\\Models\\User', 8, 'auth_token', '7d74fce98845261caf73d73ecf463086e364aa1e11f0b8db48271bec448bdcff', '[\"*\"]', '2026-03-31 10:57:45', NULL, '2026-03-31 10:57:36', '2026-03-31 10:57:45'),
(28, 'App\\Models\\User', 5, 'auth_token', '56c93845ae686e4c181e871f607616de6b193998c85615b3201f351aefb358cf', '[\"*\"]', NULL, NULL, '2026-03-31 18:02:55', '2026-03-31 18:02:55'),
(29, 'App\\Models\\User', 8, 'auth_token', '1896e199e27e9d7cc725cb34560bffe69b994589b6ce393e426d825cee92ed33', '[\"*\"]', '2026-03-31 19:32:33', NULL, '2026-03-31 18:29:30', '2026-03-31 19:32:33'),
(30, 'App\\Models\\User', 5, 'auth_token', 'd96ec4c0f027b6b37c3d071601263dbb389700e02a29e9f736a0647d052ca10a', '[\"*\"]', NULL, NULL, '2026-03-31 20:11:02', '2026-03-31 20:11:02'),
(31, 'App\\Models\\User', 5, 'auth_token', '4d461db11f2f98c0d3e3209547dfd943e64d6eb2503cecaa6ee4297cf23c2ae9', '[\"*\"]', NULL, NULL, '2026-03-31 20:13:04', '2026-03-31 20:13:04'),
(32, 'App\\Models\\User', 8, 'auth_token', '2dfb7df5b9cc372dac26644ba9fb27ecf63b223bcda04070b2cc126fe8daec8b', '[\"*\"]', '2026-03-31 20:16:56', NULL, '2026-03-31 20:15:54', '2026-03-31 20:16:56'),
(33, 'App\\Models\\User', 5, 'auth_token', 'a2c9b211645959078827eddf0e91e1a16f0aa80647bb2226f4928e954740dfe9', '[\"*\"]', '2026-03-31 22:17:01', NULL, '2026-03-31 20:18:08', '2026-03-31 22:17:01'),
(34, 'App\\Models\\User', 8, 'auth_token', '0f04d4524ea82202871138f9a877433dcb6efb86ec47f57563b3f42dc3d2c24c', '[\"*\"]', '2026-03-31 22:22:34', NULL, '2026-03-31 22:21:28', '2026-03-31 22:22:34'),
(35, 'App\\Models\\User', 5, 'auth_token', '21c10cbf9ac1696a42202fbf9ad9c8085cc7f4a15600f4d64e63a7746beefddb', '[\"*\"]', '2026-03-31 22:27:59', NULL, '2026-03-31 22:23:19', '2026-03-31 22:27:59'),
(36, 'App\\Models\\User', 5, 'auth_token', '97894539cfac4df5bc09b097cd47da1e900996dfd413fd56adb1dae6ada5e233', '[\"*\"]', '2026-03-31 22:31:03', NULL, '2026-03-31 22:31:03', '2026-03-31 22:31:03'),
(37, 'App\\Models\\User', 5, 'auth_token', '525d4d5937135c9fe797f102abaa4c3ac319336a98954da15ebd5c97b46d58a7', '[\"*\"]', NULL, NULL, '2026-03-31 23:49:18', '2026-03-31 23:49:18'),
(38, 'App\\Models\\User', 8, 'auth_token', '0cfe27af16b8b05bb900e0af0c6a08eff16e26ffbab80b39fae3f3cf5074f1ff', '[\"*\"]', '2026-03-31 23:51:35', NULL, '2026-03-31 23:50:44', '2026-03-31 23:51:35'),
(39, 'App\\Models\\User', 5, 'auth_token', '9b95476020ccdbc37f3aa3b7dcf5a6da12f02dcdf815f39e762d8590c9b8e372', '[\"*\"]', '2026-04-01 10:05:04', NULL, '2026-03-31 23:52:45', '2026-04-01 10:05:04'),
(40, 'App\\Models\\User', 8, 'auth_token', '84e4e0ee4de8a0bf03dcdffad16f97d2665b4074e6858019ed863d33f4a1c37a', '[\"*\"]', '2026-04-01 10:07:01', NULL, '2026-04-01 10:05:30', '2026-04-01 10:07:01'),
(41, 'App\\Models\\User', 5, 'auth_token', 'ba49b9f735f4fbadf3c81659c6a3f66573b3c886e51716b0363b29684544d8b1', '[\"*\"]', '2026-04-01 10:25:50', NULL, '2026-04-01 10:07:20', '2026-04-01 10:25:50'),
(42, 'App\\Models\\User', 8, 'auth_token', 'd25dd7c8aa35cfb2f37462fda511c8043a8886db26504283f765f04a721d2186', '[\"*\"]', '2026-04-01 10:27:24', NULL, '2026-04-01 10:26:19', '2026-04-01 10:27:24'),
(43, 'App\\Models\\User', 5, 'auth_token', 'e48c77425945cdcb2663ff12a2823fe4e41182947fb9975fa4063610cd4fc6ba', '[\"*\"]', '2026-04-01 11:02:11', NULL, '2026-04-01 10:28:26', '2026-04-01 11:02:11'),
(44, 'App\\Models\\User', 8, 'auth_token', '10e92ff3a99eee5018b9a0e4f8b161f6be449570571d5991a00466089f01a903', '[\"*\"]', '2026-04-01 11:03:32', NULL, '2026-04-01 11:02:37', '2026-04-01 11:03:32'),
(45, 'App\\Models\\User', 5, 'auth_token', 'ec965e45fe387f16677014b14a01df78b0e6ee5776ff5ff82322f3ecfdd9acf2', '[\"*\"]', '2026-04-01 11:27:41', NULL, '2026-04-01 11:04:01', '2026-04-01 11:27:41'),
(46, 'App\\Models\\User', 8, 'auth_token', '3a4e6f85240fcfa040c7059eae78a38de9e3ec4fe885d542214cfe706d56f19c', '[\"*\"]', '2026-04-01 12:19:32', NULL, '2026-04-01 12:18:41', '2026-04-01 12:19:32');

-- --------------------------------------------------------

--
-- Table structure for table `products`
--

CREATE TABLE `products` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `category_id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `slug` varchar(255) DEFAULT NULL,
  `image` varchar(255) DEFAULT NULL,
  `price` decimal(10,2) NOT NULL DEFAULT 0.00,
  `discount_price` decimal(10,2) NOT NULL DEFAULT 0.00 COMMENT 'Giá khuyến mãi (Lớn hơn 0 là có sale)',
  `unit` varchar(50) NOT NULL COMMENT 'Kg, Gram, Bó...',
  `description` longtext DEFAULT NULL,
  `origin` varchar(100) DEFAULT NULL COMMENT 'Xuất xứ',
  `is_active` tinyint(1) NOT NULL DEFAULT 1 COMMENT '1: Đang bán, 0: Ngừng bán',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `products`
--

INSERT INTO `products` (`id`, `category_id`, `name`, `slug`, `image`, `price`, `discount_price`, `unit`, `description`, `origin`, `is_active`, `created_at`, `updated_at`) VALUES
(17, 19, 'Rau muống tươi', 'rau-muong-tuoi', 'products/4a2WuCzesSnAgqz4371OUVVWpHBTNvg79iAJexPS.jpg', 18000.00, 15000.00, 'Bó', 'Bó rau muống tươi, rửa sạch, không thuốc bảo vệ thực vật', 'Hà Nội', 1, '2026-03-17 10:38:12', '2026-03-23 19:00:57'),
(18, 19, 'Cải bó xôi', 'cai-bo-xoi', 'products/UBwOLEA7MYkaimUsgS8WimJnLeWvm35TXZ3WvkHI.jpg', 45000.00, 0.00, 'Kg', 'Lá xanh đậm, giàu sắt, thu hoạch trong ngày', 'Hưng Yên', 1, '2026-03-17 10:40:02', '2026-03-23 19:00:27'),
(19, 19, 'Cà rốt hữu cơ', 'ca-rot-huu-co', 'products/aw4R1R3fP6hRd2xYEYn585QTpvtfZOvS2ecSxdeu.jpg', 55000.00, 0.00, 'Kg', 'Cà rốt hữu cơ, vỏ mỏng, ngọt tự nhiên', 'Hải Dương', 1, '2026-03-17 10:41:04', '2026-03-23 18:59:58'),
(20, 19, 'Khoai tây đỏ', 'khoai-tay-do', 'products/byczEmFIIaKKPKnAyCmwOMQPqitE12bIhXOECT4g.jpg', 32000.00, 28000.00, 'Kg', 'Khoai tây tươi, vỏ mỏng, phù hợp chiên/nướng', 'Sơn La', 1, '2026-03-17 10:41:41', '2026-03-23 18:59:29'),
(21, 19, 'Bí đỏ', 'bi-do', 'products/vInX8ftwaabRk2ZM6YEcNE4c4qtELYrqZGzkYV40.jpg', 28000.00, 0.00, 'Kg', 'Bí đỏ tươi, thịt dày, dùng nấu canh hoặc làm bánh', 'Bắc Giang', 1, '2026-03-17 10:42:43', '2026-03-23 18:59:03'),
(22, 20, 'Táo đỏ (hộp 1kg)', 'tao-do-hop-1kg', 'products/AcHqO4CzW5nEfK1q0bcPWO9XZTjTN2ZKs8p3Gb1J.jpg', 120000.00, 99000.00, 'Hộp', 'Táo giòn, ngọt vừa, không thuốc bảo vệ dư lượng cao', 'Lâm Đồng', 1, '2026-03-17 10:44:17', '2026-03-23 18:58:33'),
(23, 20, 'Chuối tiêu', 'chuoi-tieu', 'products/ENnfAMc1tDRwKEkrsYqCd3yOFcYK1nuIwFBdRJlx.jpg', 40000.00, 0.00, 'Nải', 'Chuối chín vừa, thơm, thích hợp ăn trực tiếp', 'Tiền Giảng', 1, '2026-03-17 10:46:04', '2026-03-23 18:58:06'),
(24, 20, 'Xoài cát', 'xoai-cat', 'products/nz5pAjVvmOzPs5RWPgOcaiZSt8vAOmGo1biNi7o8.jpg', 65000.00, 55000.00, 'Kg', 'Xoài chín cây, thịt vàng, thơm mùi đặc trưng', 'Tiền Giang', 1, '2026-03-17 10:46:57', '2026-03-23 18:57:13'),
(25, 20, 'Cam sành', 'cam-sanh', 'products/7MYj9IvImNNAnL5S3Zl0FlPMCqOBfVj2HSyzN8wr.jpg', 48000.00, 42000.00, 'Kg', 'Cam mọng nước, vị chua ngọt cân bằng', 'Vĩnh Long', 1, '2026-03-17 10:47:43', '2026-03-23 18:56:27'),
(26, 20, 'Nho không hạt(Khay 500g)', 'nho-khong-hatkhay-500g', 'products/i0xHrzhuXRReCE03UIMrJIhNjupk5Q8SpTFkniAO.jpg', 150000.00, 129000.00, 'Khay', 'Nho ngọt, hạt nhỏ hoặc không hạt, đóng khay', 'Nhin Thuận', 1, '2026-03-17 10:49:21', '2026-03-23 18:56:03'),
(27, 21, 'Thịt ba chỉ heo', 'thit-ba-chi-heo', 'products/yRVM4cd5RRyzokP3IhY6jKJqtwmkbF3yViXI2G5X.jpg', 160000.00, 140000.00, 'Kg', 'Thịt ba chỉ tươi, có lớp mỡ vừa, phù hợp nướng/ram', 'Hưng Yên', 1, '2026-03-17 10:51:28', '2026-03-23 18:55:15'),
(28, 21, 'Ức gà không da', 'uc-ga-khong-da', 'products/rjYBWfgyUWUmyjNX40tOA4ewYTD4iA8UlfNlRB9b.jpg', 120000.00, 0.00, 'Kg', 'Ức gà tươi, ít mỡ, phù hợp ăn kiêng và nấu nhanh', 'Đồng Nai', 1, '2026-03-17 10:52:34', '2026-03-23 18:54:52'),
(29, 21, 'Sườn bò', 'suon-bo', 'products/DE7NCzMVpUDREuc6rPZc1xYUALL5B4cJFZhAf6vn.jpg', 320000.00, 290000.00, 'Kg', 'Sườn bò tươi, thịt chắc, thích hợp nướng hoặc hầm', 'Bình Dương', 1, '2026-03-17 10:53:34', '2026-03-23 18:54:22'),
(30, 21, 'Thịt nạc vai heo', 'thit-nac-vai-heo', 'products/TL9UoLUlDpOf6M0XfMw7Ls75zR1AMvVpqdnTyGWY.jpg', 150000.00, 135000.00, 'Kg', 'Nạc vai mềm, thích hợp xay, kho, chiên', 'Thái Bình', 1, '2026-03-17 10:54:36', '2026-03-23 18:54:00'),
(31, 21, 'Gà ta thả vườn (nguyên con ~1.6kg)', 'ga-ta-tha-vuon-nguyen-con-16kg', 'products/J2uehHEOH3RWjUNPQIYqNiyKgdpUGKCogpV3IRJI.jpg', 220000.00, 199000.00, 'con', 'Gà thả vườn, thịt săn chắc, vị ngọt tự nhiên', 'Bắc Ninh', 1, '2026-03-17 10:56:27', '2026-03-23 18:53:31'),
(32, 22, 'Cá hồi phi lê', 'ca-hoi-phi-le', 'products/tQ7m11rmw9tFW5j6yoCp8by48neLdBkn0SvvzfVl.jpg', 520000.00, 480000.00, 'Kg', 'Phi lê cá hồi tươi, không xương, phù hợp nướng/áp chảo', 'Na Uy', 1, '2026-03-17 10:58:45', '2026-03-23 18:53:00'),
(33, 22, 'Tôm sú tươi', 'tom-su-tuoi', 'products/S65TRh7aQEgXpwGGyZGgg3OhPGSYHRVQBS3dHMJu.jpg', 380000.00, 340000.00, 'Kg', 'Tôm sú size trung, vỏ tươi, thích hợp hấp/nướng', 'Cà Mau', 1, '2026-03-17 10:59:24', '2026-03-23 18:52:33'),
(34, 22, 'Cá thu một nắng', 'ca-thu-mot-nang', 'products/SrPA4zO37lncIY9XeMtiCWriqyt19DiNVH57tN9b.jpg', 220000.00, 0.00, 'Kg', 'Cá thu phơi 1 nắng, giữ vị biển, tiện nướng', 'Phú Yên', 1, '2026-03-17 11:00:00', '2026-03-23 18:52:02'),
(35, 22, 'Mực ống tươi', 'muc-ong-tuoi', 'products/0Jrd3nBEh7kZ5quKdcf2oLDaOdb1rq6CkXaVKol7.jpg', 260000.00, 230000.00, 'Kg', 'Mực ống tươi, thịt dai, phù hợp xào, nướng', 'Quảng Ninh', 1, '2026-03-17 11:00:53', '2026-03-23 18:51:35'),
(36, 22, 'Sò điệp đông lạnh(500g)', 'so-diep-dong-lanh500g', 'products/SM8hnU0D1XtFzNv6U3Kf4NsXZ9LJqdF1ay8hIRKo.jpg', 280000.00, 249000.00, 'Khay', 'Sò điệp làm sạch, cấp đông nhanh, tiện chế biến', 'Việt Nam', 1, '2026-03-17 11:02:57', '2026-03-23 18:50:49');

-- --------------------------------------------------------

--
-- Table structure for table `product_images`
--

CREATE TABLE `product_images` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `image_path` varchar(255) NOT NULL COMMENT 'Đường dẫn lưu ảnh phụ',
  `sort_order` int(11) NOT NULL DEFAULT 0 COMMENT 'Số thứ tự để sắp xếp ảnh',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `product_images`
--

INSERT INTO `product_images` (`id`, `product_id`, `image_path`, `sort_order`, `created_at`, `updated_at`) VALUES
(98, 36, 'products/gallery/bgQz4MUFY2lThGIYRX27VhOQDYXHv7J163EBgSYP.jpg', 1, '2026-03-23 18:50:49', '2026-03-23 18:50:49'),
(99, 36, 'products/gallery/bh4AzoXgukwj2v2RMpyK59PWlRDTtEpqFK6OZymf.jpg', 2, '2026-03-23 18:50:49', '2026-03-23 18:50:49'),
(100, 35, 'products/gallery/temNwjcfkKaLFeDTlFb7OtSOHEsCGuLb7B7M9x6a.jpg', 1, '2026-03-23 18:51:35', '2026-03-23 18:51:35'),
(101, 35, 'products/gallery/qxBKCtFSR7IsP53mpxUUUNZc5HCTm9LGyYxe8Aji.jpg', 2, '2026-03-23 18:51:35', '2026-03-23 18:51:35'),
(102, 34, 'products/gallery/F6uNWYdgw3e8l2ceijND5KnxFfoyE8GxtkdqjCrS.jpg', 1, '2026-03-23 18:52:02', '2026-03-23 18:52:02'),
(103, 34, 'products/gallery/JH0iPlD0X02Ou6l1m79LKodtGNrSPF5N3s83vgww.jpg', 2, '2026-03-23 18:52:02', '2026-03-23 18:52:02'),
(104, 33, 'products/gallery/uKgDLvQuTh2x9MIIBmn4QUgRlUY2e5SM6sOhRxmh.jpg', 1, '2026-03-23 18:52:33', '2026-03-23 18:52:33'),
(105, 33, 'products/gallery/N4ARnyBSlQR0UNztgCIhVCEbT5KWr0x7vXjnrPsb.jpg', 2, '2026-03-23 18:52:33', '2026-03-23 18:52:33'),
(106, 32, 'products/gallery/S5eVfjQmR7TliMr6ZTL5YZ02ChPgg5HC7BSXzD9L.jpg', 1, '2026-03-23 18:53:00', '2026-03-23 18:53:00'),
(107, 32, 'products/gallery/cNDMC4n6QFGzR04iLoNV5D9W8gA4MQ6eZL0yE0rq.jpg', 2, '2026-03-23 18:53:00', '2026-03-23 18:53:00'),
(108, 31, 'products/gallery/UUlAP0Xl5yRm6Xme4GNIu1Ib5SkQNqhMiHJWRsIB.jpg', 1, '2026-03-23 18:53:31', '2026-03-23 18:53:31'),
(109, 31, 'products/gallery/5q2P07c1lXDSz8ap4sjKwKK7pFyOqnKHPBNF7RPY.jpg', 2, '2026-03-23 18:53:31', '2026-03-23 18:53:31'),
(110, 30, 'products/gallery/UcUNGrkLM88rjdW9YbSwBEF2oPydn9aQizn7u9LD.jpg', 1, '2026-03-23 18:54:00', '2026-03-23 18:54:00'),
(111, 30, 'products/gallery/b1Yatksx2WeW41xxKTubG1tlSWV8Jo5CLEPvRsoD.jpg', 2, '2026-03-23 18:54:00', '2026-03-23 18:54:00'),
(112, 29, 'products/gallery/65jZ8D0x5M4saQXT5IUj0ERpzszA5IBfS2M5snXj.jpg', 1, '2026-03-23 18:54:22', '2026-03-23 18:54:22'),
(113, 29, 'products/gallery/FicWKVhJrbFsHIBGQPXesjnChUjdjViFwO4ead57.jpg', 2, '2026-03-23 18:54:22', '2026-03-23 18:54:22'),
(114, 28, 'products/gallery/gXzFbv8Lqxk1Nb6GdnLYBGdGTIpRQk5jr8Ktr3Jt.jpg', 1, '2026-03-23 18:54:52', '2026-03-23 18:54:52'),
(115, 28, 'products/gallery/ngibWneLlEJRmRh71T3QMrJe39tS3ReEEEqGdYRA.jpg', 2, '2026-03-23 18:54:52', '2026-03-23 18:54:52'),
(116, 27, 'products/gallery/Ymp0h00bQVuS1Mz97cVMfNdcY048c0TNKMBNzuBB.jpg', 1, '2026-03-23 18:55:15', '2026-03-23 18:55:15'),
(117, 27, 'products/gallery/d8HmoFEH3vfT7rO3gj2sweMEEX7hDRKRmOWDk0nT.jpg', 2, '2026-03-23 18:55:15', '2026-03-23 18:55:15'),
(118, 26, 'products/gallery/lIkxBGNdMDhNRzFotc1AYwZYKtvaU14F07F4224i.jpg', 1, '2026-03-23 18:56:03', '2026-03-23 18:56:03'),
(119, 26, 'products/gallery/TSE2M56SPWis3tKDe1AuSJSzu72ugBvwUfUkRsK5.jpg', 2, '2026-03-23 18:56:03', '2026-03-23 18:56:03'),
(120, 25, 'products/gallery/wACHhge2SafhnFWAgTYJ99LOCwjPXesIDX1kDZfm.jpg', 1, '2026-03-23 18:56:27', '2026-03-23 18:56:27'),
(121, 25, 'products/gallery/VclmgREku1I8a4GTwsbGMJm2AJeFdA4aIm1YS84n.jpg', 2, '2026-03-23 18:56:27', '2026-03-23 18:56:27'),
(122, 24, 'products/gallery/N0P3Lf4RL5FTQiysfJZbecdWnJjMxM76eBfXocGv.jpg', 1, '2026-03-23 18:57:13', '2026-03-23 18:57:13'),
(123, 24, 'products/gallery/W1Pkvspu0TRUkuVdiISCnSSnmLMTU8MEMWoeyuSc.jpg', 2, '2026-03-23 18:57:13', '2026-03-23 18:57:13'),
(124, 23, 'products/gallery/FFyIkj8RvNt9mVMfAZE5tFwoqtxNjieqepNZkrfa.jpg', 1, '2026-03-23 18:58:06', '2026-03-23 18:58:06'),
(125, 23, 'products/gallery/x7j4cbWBLdV6mCAKNCVzuZGH1b1nXzZ9Ve4ftBc4.jpg', 2, '2026-03-23 18:58:06', '2026-03-23 18:58:06'),
(126, 22, 'products/gallery/IZwZaFFjZ3T0NFd7RTvkovenx0AxH86XUCM5w4ze.jpg', 1, '2026-03-23 18:58:33', '2026-03-23 18:58:33'),
(127, 22, 'products/gallery/GksKBOFDLn06hOjpe0PpPsOQOZ3z4sRlnXJS6aJB.jpg', 2, '2026-03-23 18:58:33', '2026-03-23 18:58:33'),
(128, 21, 'products/gallery/EwgEKM9wROrDMCc4MRbr5lhX2wvA9LnY26R14v6D.jpg', 1, '2026-03-23 18:59:03', '2026-03-23 18:59:03'),
(129, 21, 'products/gallery/MogZEGydSK5t3MGOXVzBlX3OUk9eypxrsU55nmTZ.jpg', 2, '2026-03-23 18:59:03', '2026-03-23 18:59:03'),
(130, 20, 'products/gallery/DNbQLGmK5RB0zfixKvFTOdKGLCaSsVjwuwxrZibG.jpg', 1, '2026-03-23 18:59:29', '2026-03-23 18:59:29'),
(131, 20, 'products/gallery/wHVIo8FvHMZY4A32HuzbwqDM6MRheVRIqLIgDPqj.jpg', 2, '2026-03-23 18:59:29', '2026-03-23 18:59:29'),
(132, 19, 'products/gallery/psCys3IAXCb6LDnAWKHRtsxK6oA1XcFUMoDHkvkl.jpg', 1, '2026-03-23 18:59:59', '2026-03-23 18:59:59'),
(133, 19, 'products/gallery/y9H3kgdpwIzoOOOeVjFR5D5CSnU4rVZDAz2aHRAN.jpg', 2, '2026-03-23 18:59:59', '2026-03-23 18:59:59'),
(134, 18, 'products/gallery/vWFh6x4pwYlpQ4Ag9zeTUnFkbzCti7H3mNk1K9KU.jpg', 1, '2026-03-23 19:00:27', '2026-03-23 19:00:27'),
(135, 18, 'products/gallery/1F12hUAWDLw6Mip1NxF0nVUUSw1P4KSu2m8Nywu3.jpg', 2, '2026-03-23 19:00:27', '2026-03-23 19:00:27'),
(136, 17, 'products/gallery/NKBZaBZ2x1tDETkkDdmtOesSXlxXLBfYLv1wGSqe.jpg', 1, '2026-03-23 19:00:57', '2026-03-23 19:00:57'),
(137, 17, 'products/gallery/ma8PVKCpwiy0v99ForkP2S0UbZngSjAF0QYMCP6f.jpg', 2, '2026-03-23 19:00:58', '2026-03-23 19:00:58');

-- --------------------------------------------------------

--
-- Table structure for table `reviews`
--

CREATE TABLE `reviews` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `user_id` bigint(20) UNSIGNED NOT NULL,
  `product_id` bigint(20) UNSIGNED NOT NULL,
  `rating` tinyint(3) UNSIGNED NOT NULL DEFAULT 5 COMMENT 'Số sao: 1 đến 5',
  `status` enum('pending','approved','hidden') NOT NULL DEFAULT 'pending',
  `comment` text DEFAULT NULL COMMENT 'Nội dung đánh giá',
  `admin_reply` text DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sessions`
--

CREATE TABLE `sessions` (
  `id` varchar(255) NOT NULL,
  `user_id` bigint(20) UNSIGNED DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `user_agent` text DEFAULT NULL,
  `payload` longtext NOT NULL,
  `last_activity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `sessions`
--

INSERT INTO `sessions` (`id`, `user_id`, `ip_address`, `user_agent`, `payload`, `last_activity`) VALUES
('Uttd5GQvsDov1oM5LIfweOsPIbgeEWxfATLBJ8Va', NULL, '127.0.0.1', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/146.0.0.0 Safari/537.36', 'YTozOntzOjY6Il90b2tlbiI7czo0MDoidjdFQmVzcmhiUTltbUNad3N0Q3dodG9nR25zTVBDTEFWemVvZWN1SiI7czo5OiJfcHJldmlvdXMiO2E6Mjp7czozOiJ1cmwiO3M6MzQ6Imh0dHA6Ly8xMjcuMC4wLjE6ODAwMC9hZG1pbi9vcmRlcnMiO3M6NToicm91dGUiO3M6MTg6ImFkbWluLm9yZGVycy5pbmRleCI7fXM6NjoiX2ZsYXNoIjthOjI6e3M6Mzoib2xkIjthOjA6e31zOjM6Im5ldyI7YTowOnt9fX0=', 1775072800);

-- --------------------------------------------------------

--
-- Table structure for table `suppliers`
--

CREATE TABLE `suppliers` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `contact_name` varchar(100) DEFAULT NULL,
  `phone` varchar(15) DEFAULT NULL,
  `address` text NOT NULL,
  `is_active` tinyint(1) NOT NULL DEFAULT 1,
  `certificate` text DEFAULT NULL COMMENT 'Chứng chỉ VietGAP, Organic...',
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `suppliers`
--

INSERT INTO `suppliers` (`id`, `name`, `contact_name`, `phone`, `address`, `is_active`, `certificate`, `created_at`, `updated_at`) VALUES
(3, 'Công ty CP Nông nghiệp VinEco (Vingroup)', 'Phòng Kinh doanh', '0823447566', 'Khu đô thị Vinhomes, Hà Nội (Vingroup)', 1, 'VietGAP, tiêu chuẩn nội bộ VinEco', '2026-03-18 02:12:00', '2026-03-17 19:20:12'),
(4, 'TH True Milk (Tập đoàn TH)', 'Phòng Bán hàng & Đối tác', '0855218790', 'Nghĩa Đàn, Nghệ An (Trang trại TH)', 1, 'HACCP, ISO, Chứng nhận sữa tươi sạch', '2026-03-18 02:12:00', '2026-03-17 19:20:33'),
(5, 'Công ty CP Thủy sản Minh Phú', 'Phòng Xuất khẩu', '0899476881', 'Cà Mau (Nhà máy chế biến thủy sản Minh Phú)', 1, 'HACCP, BRC (xuất khẩu)', '2026-03-18 02:12:00', '2026-03-17 19:20:48'),
(6, 'CP Vietnam (C.P. Vietnam Corporation)', 'Phòng Cung ứng thực phẩm', '0822745572', 'KCN Đồng Văn, Hà Nam (cơ sở chăn nuôi/giết mổ)', 1, 'VietGAP, GlobalGAP (một số sản phẩm)', '2026-03-18 02:12:00', '2026-03-17 19:21:06'),
(7, 'Vina T&T Group (nhập khẩu & phân phối nông sản)', 'Phòng Kinh doanh Xuất nhập khẩu', '0899211544', 'TP. Hồ Chí Minh (Vina T&T)', 1, 'Chứng nhận kiểm dịch, HACCP (một số mặt hàng)', '2026-03-18 02:12:00', '2026-03-17 19:21:27'),
(8, 'HTX Nông nghiệp Hữu cơ Lâm Đồng', 'Ban Quản lý HTX', '0887233419', 'Lâm Đồng (vùng trồng rau quả hữu cơ)', 1, 'Organic (cấp địa phương), VietGAP', '2026-03-18 02:12:00', '2026-03-17 19:21:54'),
(9, 'Công ty TNHH Sản xuất Thực phẩm GreenFarm', 'Phòng Kinh doanh', '0863223801', 'Long An / Đồng Nai (cơ sở chế biến thực phẩm sạch)', 1, 'VietGAP, HACCP', '2026-03-18 02:12:00', '2026-03-17 19:22:26'),
(10, 'Công ty TNHH Hải sản Cà Mau', 'Phòng Cung ứng', '0822487199', 'Cà Mau (cảng/nhà máy sơ chế)', 1, 'HACCP, chứng nhận kiểm dịch xuất khẩu', '2026-03-18 02:12:00', '2026-03-17 19:22:46');

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) UNSIGNED NOT NULL,
  `fullname` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `address` text DEFAULT NULL,
  `role` enum('admin','customer','shipper') NOT NULL DEFAULT 'customer',
  `status` enum('active','locked') NOT NULL DEFAULT 'active',
  `work_status` enum('available','on_delivery','offline') DEFAULT NULL COMMENT 'Trạng thái làm việc của Shipper: sẵn sàng, đang giao, ngoại tuyến',
  `avatar` varchar(255) DEFAULT NULL,
  `email_verified_at` timestamp NULL DEFAULT NULL,
  `password` varchar(255) NOT NULL,
  `remember_token` varchar(100) DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `fullname`, `email`, `phone`, `address`, `role`, `status`, `work_status`, `avatar`, `email_verified_at`, `password`, `remember_token`, `created_at`, `updated_at`) VALUES
(3, 'Admin', 'nantibiame@gmail.com', '0896720844', '141 Đường Chiến thắng', 'admin', 'active', NULL, 'avatars/FStB7UI6wEvYNWybO9QA0obCqeWBQ3UylkS6n2vZ.png', NULL, '$2y$12$hZnxUBQguvDItFJEiMXXfeP2gLxEULF5uZ/kllOHaaFVFNjxokHc6', NULL, '2026-03-17 20:13:27', '2026-03-23 19:38:41'),
(4, 'Nanthaxay', 'pixibee.studio@gmail.com', '0834451397', 'Av. Kaysone Phomvihane', 'customer', 'active', NULL, 'avatars/UStEBrfFB6toLqeJWAXx2CK9JXB4FmNyesMlg3ZK.png', NULL, '$2y$12$YTPZQDGJQQJYyPeNoJK/C.huFOv1RL9zDqqo9o7fjDN1sc6Klj3sy', NULL, '2026-03-17 20:14:45', '2026-03-23 19:38:18'),
(5, 'Nguyễn Văn A', 'idtesting0one@gmail.com', '0876652109', '141 Đường Chiến thắng', 'shipper', 'active', 'available', 'avatars/FNeNRQo0IQW8WqibmRYMRNa6u6XmR3XrbN5vZlfX.jpg', NULL, '$2y$12$fJl06tiz4xA6qJ5DeYJrSupdwNBMhhTyJX12mX9m7JsxVlc89TfPa', NULL, '2026-03-17 20:17:40', '2026-04-01 11:26:30'),
(8, 'Nguyễn Văn B', 'helloworld1x4010@gmail.com', NULL, NULL, 'customer', 'active', NULL, 'avatars/CKm72mLTU50vVoaISsWYGY2ZlxE3LyHUcGC6VLpp.png', NULL, '$2y$12$aCIYrydyhIjajfwz4rE3h.EzGq1Tz4o2yk1FDqO5FjpzBZpeAoVrK', NULL, '2026-03-26 10:19:57', '2026-03-28 12:35:41');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `batches`
--
ALTER TABLE `batches`
  ADD PRIMARY KEY (`id`),
  ADD KEY `batches_product_id_foreign` (`product_id`),
  ADD KEY `batches_supplier_id_foreign` (`supplier_id`);

--
-- Indexes for table `cache`
--
ALTER TABLE `cache`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_expiration_index` (`expiration`);

--
-- Indexes for table `cache_locks`
--
ALTER TABLE `cache_locks`
  ADD PRIMARY KEY (`key`),
  ADD KEY `cache_locks_expiration_index` (`expiration`);

--
-- Indexes for table `carts`
--
ALTER TABLE `carts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `carts_user_id_foreign` (`user_id`),
  ADD KEY `carts_product_id_foreign` (`product_id`);

--
-- Indexes for table `categories`
--
ALTER TABLE `categories`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `failed_jobs_uuid_unique` (`uuid`);

--
-- Indexes for table `jobs`
--
ALTER TABLE `jobs`
  ADD PRIMARY KEY (`id`),
  ADD KEY `jobs_queue_index` (`queue`);

--
-- Indexes for table `job_batches`
--
ALTER TABLE `job_batches`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `migrations`
--
ALTER TABLE `migrations`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `orders`
--
ALTER TABLE `orders`
  ADD PRIMARY KEY (`id`),
  ADD KEY `orders_user_id_foreign` (`user_id`),
  ADD KEY `orders_shipper_id_foreign` (`shipper_id`);

--
-- Indexes for table `order_details`
--
ALTER TABLE `order_details`
  ADD PRIMARY KEY (`id`),
  ADD KEY `order_details_order_id_foreign` (`order_id`),
  ADD KEY `order_details_product_id_foreign` (`product_id`),
  ADD KEY `order_details_batch_id_foreign` (`batch_id`);

--
-- Indexes for table `password_reset_tokens`
--
ALTER TABLE `password_reset_tokens`
  ADD PRIMARY KEY (`email`);

--
-- Indexes for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `personal_access_tokens_token_unique` (`token`),
  ADD KEY `personal_access_tokens_tokenable_type_tokenable_id_index` (`tokenable_type`,`tokenable_id`),
  ADD KEY `personal_access_tokens_expires_at_index` (`expires_at`);

--
-- Indexes for table `products`
--
ALTER TABLE `products`
  ADD PRIMARY KEY (`id`),
  ADD KEY `products_category_id_foreign` (`category_id`);

--
-- Indexes for table `product_images`
--
ALTER TABLE `product_images`
  ADD PRIMARY KEY (`id`),
  ADD KEY `product_images_product_id_foreign` (`product_id`);

--
-- Indexes for table `reviews`
--
ALTER TABLE `reviews`
  ADD PRIMARY KEY (`id`),
  ADD KEY `reviews_user_id_foreign` (`user_id`),
  ADD KEY `reviews_product_id_foreign` (`product_id`);

--
-- Indexes for table `sessions`
--
ALTER TABLE `sessions`
  ADD PRIMARY KEY (`id`),
  ADD KEY `sessions_user_id_index` (`user_id`),
  ADD KEY `sessions_last_activity_index` (`last_activity`);

--
-- Indexes for table `suppliers`
--
ALTER TABLE `suppliers`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `users_email_unique` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `batches`
--
ALTER TABLE `batches`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=24;

--
-- AUTO_INCREMENT for table `carts`
--
ALTER TABLE `carts`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `categories`
--
ALTER TABLE `categories`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `failed_jobs`
--
ALTER TABLE `failed_jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `jobs`
--
ALTER TABLE `jobs`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `migrations`
--
ALTER TABLE `migrations`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=21;

--
-- AUTO_INCREMENT for table `orders`
--
ALTER TABLE `orders`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `order_details`
--
ALTER TABLE `order_details`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=22;

--
-- AUTO_INCREMENT for table `personal_access_tokens`
--
ALTER TABLE `personal_access_tokens`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=47;

--
-- AUTO_INCREMENT for table `products`
--
ALTER TABLE `products`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=37;

--
-- AUTO_INCREMENT for table `product_images`
--
ALTER TABLE `product_images`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=138;

--
-- AUTO_INCREMENT for table `reviews`
--
ALTER TABLE `reviews`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `suppliers`
--
ALTER TABLE `suppliers`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `batches`
--
ALTER TABLE `batches`
  ADD CONSTRAINT `batches_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `batches_supplier_id_foreign` FOREIGN KEY (`supplier_id`) REFERENCES `suppliers` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `carts`
--
ALTER TABLE `carts`
  ADD CONSTRAINT `carts_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `carts_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `orders`
--
ALTER TABLE `orders`
  ADD CONSTRAINT `orders_shipper_id_foreign` FOREIGN KEY (`shipper_id`) REFERENCES `users` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `orders_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `order_details`
--
ALTER TABLE `order_details`
  ADD CONSTRAINT `order_details_batch_id_foreign` FOREIGN KEY (`batch_id`) REFERENCES `batches` (`id`) ON DELETE SET NULL,
  ADD CONSTRAINT `order_details_order_id_foreign` FOREIGN KEY (`order_id`) REFERENCES `orders` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `order_details_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `products`
--
ALTER TABLE `products`
  ADD CONSTRAINT `products_category_id_foreign` FOREIGN KEY (`category_id`) REFERENCES `categories` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `product_images`
--
ALTER TABLE `product_images`
  ADD CONSTRAINT `product_images_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE;

--
-- Constraints for table `reviews`
--
ALTER TABLE `reviews`
  ADD CONSTRAINT `reviews_product_id_foreign` FOREIGN KEY (`product_id`) REFERENCES `products` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `reviews_user_id_foreign` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
