package com.pixibeestudio.greenly.ui.fragment;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.gson.JsonObject;
import com.pixibeestudio.greenly.R;
import com.pixibeestudio.greenly.data.network.RetrofitClient;
import com.pixibeestudio.greenly.ui.adapter.SelectedImageAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn Viết đánh giá sản phẩm.
 * - Hiển thị thông tin sản phẩm, rating bar, ô text (max 500 ký tự), upload ảnh (max 5)
 * - Nút "Đánh giá sau" / "Gửi" - cả 2 đều hiển thị dialog xác nhận khi có thay đổi
 * - Click Back (hardware hoặc toolbar) cũng hiển thị dialog nếu có thay đổi
 */
public class WriteReviewFragment extends Fragment {

    private static final String TAG = "WriteReviewFragment";
    private static final int MAX_IMAGES = 5;

    // Args
    private int orderDetailId;
    private String productName;
    private String productImage;
    private int quantity;
    private double price;

    // Views
    private ImageButton btnBack;
    private ShapeableImageView ivProduct;
    private TextView tvProductName, tvProductPrice, tvProductQty, tvCharCounter;
    private RatingBar ratingBar;
    private EditText edtComment;
    private RecyclerView rvSelectedImages;
    private MaterialButton btnReviewLater, btnSubmit;

    // Data
    private final List<Uri> selectedImages = new ArrayList<>();
    private SelectedImageAdapter imageAdapter;
    private Uri tempCameraUri; // lưu uri file tạm cho camera

    // Activity Result Launchers
    private ActivityResultLauncher<String> cameraPermissionLauncher;
    private ActivityResultLauncher<Uri> takePictureLauncher;
    private ActivityResultLauncher<PickVisualMediaRequest> pickImageLauncher;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderDetailId = getArguments().getInt("orderDetailId", 0);
            productName = getArguments().getString("productName", "");
            productImage = getArguments().getString("productImage", "");
            quantity = getArguments().getInt("quantity", 1);
            price = getArguments().getFloat("price", 0f);
        }
        registerLaunchers();
    }

    /**
     * Đăng ký các launcher cho: permission camera, chụp ảnh, chọn ảnh từ gallery.
     * Phải đăng ký trong onCreate() theo yêu cầu của ActivityResultContracts.
     */
    private void registerLaunchers() {
        // 1) Xin quyền Camera
        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        launchCamera();
                    } else {
                        Toast.makeText(requireContext(),
                                "Bạn cần cấp quyền Camera để chụp ảnh", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // 2) Chụp ảnh từ camera
        takePictureLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success && tempCameraUri != null) {
                        addImage(tempCameraUri);
                    }
                }
        );

        // 3) Chọn ảnh từ thư viện (PhotoPicker - không cần permission Android 13+)
        pickImageLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri != null) {
                        addImage(uri);
                    }
                }
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_write_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        bindProductInfo();
        setupImagePicker();
        setupListeners();
        handleBackPressed();
    }

    private void bindViews(View v) {
        btnBack = v.findViewById(R.id.btn_back_write_review);
        ivProduct = v.findViewById(R.id.iv_product_write);
        tvProductName = v.findViewById(R.id.tv_product_name_write);
        tvProductPrice = v.findViewById(R.id.tv_product_price_write);
        tvProductQty = v.findViewById(R.id.tv_product_qty_write);
        tvCharCounter = v.findViewById(R.id.tv_char_counter);
        ratingBar = v.findViewById(R.id.rating_bar);
        edtComment = v.findViewById(R.id.edt_review_comment);
        rvSelectedImages = v.findViewById(R.id.rv_selected_images);
        btnReviewLater = v.findViewById(R.id.btn_review_later);
        btnSubmit = v.findViewById(R.id.btn_submit_review);
    }

    private void bindProductInfo() {
        tvProductName.setText(productName);
        tvProductQty.setText("x" + quantity);

        NumberFormat format = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("vi-VN"));
        tvProductPrice.setText(format.format(price));

        if (productImage != null && !productImage.isEmpty()) {
            String fullUrl = productImage.startsWith("http")
                    ? productImage
                    : RetrofitClient.BASE_URL + productImage.replaceFirst("^/", "");
            Glide.with(this)
                    .load(fullUrl)
                    .placeholder(R.drawable.ic_default_product)
                    .error(R.drawable.ic_default_product)
                    .centerCrop()
                    .into(ivProduct);
        }
    }

    private void setupImagePicker() {
        imageAdapter = new SelectedImageAdapter(selectedImages, new SelectedImageAdapter.OnImageActionListener() {
            @Override
            public void onAddClick() {
                openImagePicker();
            }

            @Override
            public void onRemoveClick(int position) {
                if (position >= 0 && position < selectedImages.size()) {
                    selectedImages.remove(position);
                    imageAdapter.notifyDataSetChanged();
                }
            }
        });
        rvSelectedImages.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        rvSelectedImages.setAdapter(imageAdapter);
    }

    private void setupListeners() {
        // Character counter cho ô comment
        edtComment.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                tvCharCounter.setText(s.length() + "/500");
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        btnBack.setOnClickListener(v -> tryExit());
        btnReviewLater.setOnClickListener(v -> tryExit());
        btnSubmit.setOnClickListener(v -> submitReview());
    }

    /**
     * Bắt nút Back vật lý để hiển thị dialog xác nhận.
     */
    private void handleBackPressed() {
        requireActivity().getOnBackPressedDispatcher().addCallback(
                getViewLifecycleOwner(),
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        tryExit();
                    }
                }
        );
    }

    /**
     * Hiển thị BottomSheet để chọn camera hoặc gallery.
     */
    private void openImagePicker() {
        if (selectedImages.size() >= MAX_IMAGES) {
            Toast.makeText(requireContext(),
                    "Chỉ được tải lên tối đa 5 ảnh", Toast.LENGTH_SHORT).show();
            return;
        }

        ImagePickerBottomSheet sheet = new ImagePickerBottomSheet();
        sheet.setOnPickListener(new ImagePickerBottomSheet.OnPickListener() {
            @Override
            public void onPickCamera() {
                requestCameraAndCapture();
            }

            @Override
            public void onPickGallery() {
                // PhotoPicker không cần permission trên Android 13+
                pickImageLauncher.launch(
                        new PickVisualMediaRequest.Builder()
                                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                                .build()
                );
            }
        });
        sheet.show(getChildFragmentManager(), "imagePicker");
    }

    /**
     * Kiểm tra quyền Camera, xin nếu chưa có, rồi mở camera hệ thống.
     */
    private void requestCameraAndCapture() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    /**
     * Tạo file ảnh tạm trong cache → lấy URI qua FileProvider → gọi camera intent.
     */
    private void launchCamera() {
        try {
            File imagesDir = new File(requireContext().getCacheDir(), "images");
            if (!imagesDir.exists()) imagesDir.mkdirs();

            File imageFile = new File(imagesDir, "review_" + System.currentTimeMillis() + ".jpg");
            tempCameraUri = FileProvider.getUriForFile(
                    requireContext(),
                    requireContext().getPackageName() + ".fileprovider",
                    imageFile
            );
            takePictureLauncher.launch(tempCameraUri);
        } catch (Exception e) {
            Log.e(TAG, "Lỗi mở camera: " + e.getMessage());
            Toast.makeText(requireContext(), "Không thể mở camera", Toast.LENGTH_SHORT).show();
        }
    }

    private void addImage(Uri uri) {
        if (selectedImages.size() >= MAX_IMAGES) return;
        selectedImages.add(uri);
        imageAdapter.notifyDataSetChanged();
    }

    /**
     * Kiểm tra có nội dung thay đổi không → hiển thị dialog xác nhận thoát.
     */
    private void tryExit() {
        boolean hasContent = ratingBar.getRating() > 0
                || !edtComment.getText().toString().trim().isEmpty()
                || !selectedImages.isEmpty();

        if (!hasContent) {
            Navigation.findNavController(requireView()).popBackStack();
            return;
        }

        new AlertDialog.Builder(requireContext())
                .setTitle("Thoát đánh giá")
                .setMessage("Bạn chắc chắn muốn thoát không? Nội dung đánh giá của bạn sẽ không được lưu.")
                .setNegativeButton("Ở lại đánh giá", (dialog, which) -> dialog.dismiss())
                .setPositiveButton("Thoát", (dialog, which) -> {
                    dialog.dismiss();
                    Navigation.findNavController(requireView()).popBackStack();
                })
                .show();
    }

    /**
     * Validate + upload review qua multipart API.
     */
    private void submitReview() {
        int rating = (int) ratingBar.getRating();
        String comment = edtComment.getText().toString().trim();

        if (rating < 1) {
            Toast.makeText(requireContext(), "Vui lòng chọn số sao đánh giá", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSubmit.setEnabled(false);
        btnSubmit.setText("Đang gửi...");
        btnReviewLater.setEnabled(false);

        // Build multipart parts
        RequestBody orderDetailIdBody = RequestBody.create(
                MediaType.parse("text/plain"), String.valueOf(orderDetailId));
        RequestBody ratingBody = RequestBody.create(
                MediaType.parse("text/plain"), String.valueOf(rating));
        RequestBody commentBody = RequestBody.create(
                MediaType.parse("text/plain"), comment);

        List<MultipartBody.Part> imageParts = new ArrayList<>();
        for (Uri uri : selectedImages) {
            File file = copyUriToCacheFile(uri);
            if (file != null) {
                RequestBody fileBody = RequestBody.create(MediaType.parse("image/*"), file);
                imageParts.add(MultipartBody.Part.createFormData(
                        "images[]", file.getName(), fileBody));
            }
        }

        RetrofitClient.getApiService(requireContext())
                .createReview(orderDetailIdBody, ratingBody, commentBody, imageParts)
                .enqueue(new Callback<JsonObject>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                        if (!isAdded()) return;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Gửi");
                        btnReviewLater.setEnabled(true);

                        if (response.isSuccessful()) {
                            // Navigate sang màn cảm ơn
                            Navigation.findNavController(requireView())
                                    .navigate(R.id.action_writeReviewFragment_to_reviewSuccessFragment);
                        } else {
                            String msg = "Lỗi gửi đánh giá";
                            try {
                                if (response.errorBody() != null) {
                                    String errorStr = response.errorBody().string();
                                    JsonObject err = new com.google.gson.JsonParser().parse(errorStr).getAsJsonObject();
                                    if (err.has("message")) msg = err.get("message").getAsString();
                                }
                            } catch (Exception ignored) {}
                            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btnSubmit.setEnabled(true);
                        btnSubmit.setText("Gửi");
                        btnReviewLater.setEnabled(true);
                        Log.e(TAG, "Lỗi mạng: " + t.getMessage());
                        Toast.makeText(requireContext(), "Lỗi mạng, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * Copy Uri ảnh (gallery hoặc camera) ra file thật trong cache để upload.
     */
    private File copyUriToCacheFile(Uri uri) {
        try {
            Context ctx = requireContext();
            File dir = new File(ctx.getCacheDir(), "upload_temp");
            if (!dir.exists()) dir.mkdirs();

            File file = new File(dir, "upload_" + System.currentTimeMillis() + ".jpg");
            try (InputStream in = ctx.getContentResolver().openInputStream(uri);
                 FileOutputStream out = new FileOutputStream(file)) {
                if (in == null) return null;
                byte[] buffer = new byte[4096];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
            }
            return file;
        } catch (Exception e) {
            Log.e(TAG, "Lỗi copy uri: " + e.getMessage());
            return null;
        }
    }
}
