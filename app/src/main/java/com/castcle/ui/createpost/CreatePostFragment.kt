package com.castcle.ui.createpost

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.graphics.Color
import android.os.Environment
import android.view.*
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.castcle.android.R
import com.castcle.android.databinding.FragmentCreateBlocBinding
import com.castcle.android.databinding.ToolbarCastcleGreetingBinding
import com.castcle.common.lib.extension.subscribeOnClick
import com.castcle.common_model.model.createblog.MediaItem
import com.castcle.common_model.model.feed.toContentUiModel
import com.castcle.common_model.model.login.domain.CreatePostBundle
import com.castcle.common_model.model.userprofile.MentionUiModel
import com.castcle.common_model.model.userprofile.User
import com.castcle.components_android.ui.custom.mention.MentionView
import com.castcle.components_android.ui.custom.mention.adapter.MentionArrayAdapter
import com.castcle.extensions.*
import com.castcle.localization.LocalizedResources
import com.castcle.ui.base.*
import com.castcle.ui.common.events.ImageItemClick
import com.castcle.ui.createbloc.adapter.ImageFloxBoxAdapter
import com.castcle.ui.createbloc.adapter.ImageGalleryAdapter
import com.castcle.ui.createbloc.toMediaItemList
import com.castcle.ui.onboard.OnBoardActivity
import com.castcle.ui.onboard.OnBoardViewModel
import com.castcle.ui.onboard.navigation.OnBoardNavigator
import com.esafirm.imagepicker.features.*
import com.esafirm.imagepicker.model.Image
import com.google.android.flexbox.*
import com.permissionx.guolindev.PermissionMediator
import io.reactivex.rxkotlin.subscribeBy
import javax.inject.Inject

class CreatePostFragment : BaseFragment<CreatePostFragmentViewModel>(),
    BaseFragmentCallbacks,
    ToolbarBindingInflater<ToolbarCastcleGreetingBinding>,
    ViewBindingInflater<FragmentCreateBlocBinding> {

    @Inject lateinit var onBoardNavigator: OnBoardNavigator

    @Inject lateinit var rxPermissions: PermissionMediator

    @Inject lateinit var localizedResources: LocalizedResources

    private var softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED

    private val createBlogArgs: CreatePostFragmentArgs by navArgs()

    private val isFromProfile: Boolean
        get() = createBlogArgs.isFromProfile

    private val createPostBundle: CreatePostBundle?
        get() = createBlogArgs.createPostBundle

    private lateinit var imageGalleryAdapter: ImageGalleryAdapter

    private lateinit var imageFloxBoxAdapter: ImageFloxBoxAdapter

    private var stateOpenGallery = false

    private var imageGallerySelected: MutableList<Image> = mutableListOf()

    private lateinit var imagePickerLauncher: ImagePickerLauncher

    override val toolbarBindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> ToolbarCastcleGreetingBinding
        get() = { inflater, container, attachToRoot ->
            ToolbarCastcleGreetingBinding.inflate(inflater, container, attachToRoot)
        }
    override val toolbarBinding: ToolbarCastcleGreetingBinding
        get() = toolbarViewBinding as ToolbarCastcleGreetingBinding

    override val bindingInflater:
            (LayoutInflater, ViewGroup?, Boolean) -> FragmentCreateBlocBinding
        get() = { inflater, container, attachToRoot ->
            FragmentCreateBlocBinding.inflate(inflater, container, attachToRoot)
        }
    override val binding: FragmentCreateBlocBinding
        get() = viewBinding as FragmentCreateBlocBinding

    private val activityViewModel by lazy {
        ViewModelProvider(requireActivity(), activityViewModelFactory)
            .get(OnBoardViewModel::class.java)
    }

    override fun viewModel(): CreatePostFragmentViewModel =
        ViewModelProvider(this, viewModelFactory)
            .get(CreatePostFragmentViewModel::class.java)

    override fun onResume() {
        super.onResume()
        with(requireActivity()) {
            softInputMode = getSoftInputMode()
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        }
    }

    override fun onPause() {
        super.onPause()
        with(requireActivity()) {
            hideSoftKeyboard()
            setSoftInputMode(softInputMode)
        }
    }

    override fun initViewModel() {
    }

    override fun setupView() {
        setupToolBar()

        onBindMessageCount(Pair(0, MAX_LIGHTH))

        (context as OnBoardActivity).onGoneBottomNavigate()

        addOnBackPressedCallback {
            onBackToHomeFeed()
        }

        imagePickerLauncher = registerImagePicker { result ->
            val imageMedia = result.toMediaItemList()
            if (imageMedia.isNotEmpty()) {
                imageGallerySelected = result.toMutableList()
                onShowImagePicker()
                activatButtonOpenGallery(false)
                stateOpenGallery = false
                viewModel.input.addMediaItemSelected(imageMedia)
            }
        }
    }

    private fun setupToolBar() {
        with(toolbarBinding) {
            tvToolbarTitleAction.invisible()
            tvToolbarTitle.text = context?.getString(R.string.create_blog_toolbar_title)
            context?.getColorResource(R.color.white)?.let {
                tvToolbarTitle.setTextColor(it)
            }
            ivToolbarLogoButton
                .subscribeOnClick {
                    onBackToHomeFeed()
                }.addToDisposables()
        }
    }

    private fun onBackToHomeFeed() {
        onClearState()
        findNavController().popBackStack()
    }

    private fun onClearState() {
        viewModel.onClearState()
        stateOpenGallery = false
        imageGallerySelected = mutableListOf()
        binding.etInputMessage.setText("")
    }

    override fun bindViewEvents() {
        onBindUserMention()

        binding.btCast.subscribeOnClick {
            handlerCreateCast()
        }

        binding.tvAddImage.subscribeOnClick {
            onBindOpenGallery()
        }

        with(binding.rvImagePickger) {
            adapter = ImageGalleryAdapter().also {
                imageGalleryAdapter = it
            }
        }

        imageGalleryAdapter.itemClick.subscribe {
            onImageClick(it)
        }.addToDisposables()
    }

    private fun handlerCreateCast() {
        viewModel.createContent().subscribeBy(
            onComplete = {
                handleOnResponse()
            },
            onError = ::handleOnError
        ).addToDisposables()
    }

    override fun bindViewModel() {
        if (createPostBundle != null) {
            createPostBundle?.let {
                val usreProfile = User(
                    displayName = it.displayName,
                    avatar = it.avaterUrl,
                    castcleId = it.castcleId
                )
                viewModel.input.setUserProfile(usreProfile)
                binding.utUserBar.bindUiModel(usreProfile.toContentUiModel())
            }
        } else {
            viewModel.fetchCastUserProfile()
                .subscribe()
                .addToDisposables()
        }

        viewModel.userProfileUiModel.observe(this, {
            binding.utUserBar.bindUiModel(it)
        })

        viewModel.enableSubmitButton.subscribe {
            enableButtonCast(it)
        }.addToDisposables()

        viewModel.mediaItemImage.observe(this, {
            onBindGallery(it)
        })

        viewModel.mediaImageSelected.observe(
            this, {
                addImagePerivew(it)
            }
        )

        viewModel.messageLength.subscribe {
            onBindMessageCount(it)
        }.addToDisposables()

        viewModel.onError.subscribe {
            displayError(it)
        }.addToDisposables()

        viewModel.showLoading.subscribe {
            binding.groupLoading.visibleOrGone(it)
        }.addToDisposables()
    }

    private fun onBindMessageCount(messageCount: Pair<Int, Int>) {
        val textFormatCount = "%s/%d"
        with(binding.tvCountChar) {
            if (messageCount.first > MAX_LIGHTH) {
                setTextColor(requireContext().getColorResource(R.color.red_primary))
                text = "${messageCount.second}"
                enableButtonCast(false)
                displayErrorMessage(
                    localizedResources.getString(
                        R.string.create_cast_warning_message
                    )
                )
            } else {
                setTextColor(requireContext().getColorResource(R.color.white))
                text = textFormatCount.format(messageCount.first, messageCount.second)
            }
        }
    }

    private fun onBindOpenGallery() {
        stateOpenGallery = !stateOpenGallery
        if (stateOpenGallery) {
            activatButtonOpenGallery(stateOpenGallery)
            onShowImagePicker(true)
            requestStoragePermission {
                viewModel.fetchImageGallery().subscribeBy().addToDisposables()
            }
        } else {
            activatButtonOpenGallery(stateOpenGallery)
            onShowImagePicker()
        }
    }

    private fun onShowImagePicker(show: Boolean = false) {
        binding.rvImagePickger.visibleOrGone(show)
    }

    private fun onBindGallery(it: List<MediaItem>) {
        imageGalleryAdapter.items = it
    }

    private fun activatButtonOpenGallery(isActivated: Boolean) {
        binding.tvAddImage.isActivated = isActivated
    }

    private fun onImageClick(imageItemClick: ImageItemClick) {
        when (imageItemClick) {
            is ImageItemClick.AddImageClick -> {
                handlerImageClick(imageItemClick.itemImage)
            }

            is ImageItemClick.RemoveImageClick -> {
                viewModel.input.removeMediaItem(imageItemClick.itemImage)
                removeImageCacheSelected(imageItemClick.itemImage)
            }
        }
    }

    private fun removeImageCacheSelected(itemImage: MediaItem) {
        imageGallerySelected.find {
            it.uri.toString() == itemImage.uri
        }?.let {
            val indexImage = imageGallerySelected.indexOf(it)
            imageGallerySelected.removeAt(indexImage)
        }
    }

    private fun handlerImageClick(itemImage: MediaItem) {
        when (itemImage) {
            is MediaItem.ImageMediaItem -> {
                onUpdateImageSelected(itemImage)
                addImageCacheSelected(itemImage)
            }
            is MediaItem.OpenCamera -> {
                requestStoragePermission {
                    openGallery()
                }
            }
            is MediaItem.OpenGallery -> {}
        }
    }

    private fun addImageCacheSelected(itemImage: MediaItem) {
        imageGallerySelected.find {
            it.uri.toString() == itemImage.uri
        }?.let {
            val indexImage = imageGallerySelected.indexOf(it)
            imageGallerySelected.removeAt(indexImage)
            return
        }

        val imageSelected = Image(
            id = itemImage.id.toLong(),
            name = itemImage.displayName,
            path = itemImage.uri
        )
        imageGallerySelected.add(imageSelected)
    }

    private fun openGallery() {
        val config = ImagePickerConfig {
            mode = ImagePickerMode.MULTIPLE // default is multi image mode
            language = "en" // Set image picker language

            theme = R.style.ImagePickerTheme
            arrowColor = Color.WHITE // set toolbar arrow up color
            folderTitle = "Folder" // folder selection title
            imageTitle = "Tap to select" // image selection title
            doneButtonText = "Apply" // done button text
            limit = 4 // max images can be selected (99 by default)
            isShowCamera = true // show camera or not (true by default)
            savePath =
                ImagePickerSavePath("Camera") // captured image directory name ("Camera" folder by default)
            savePath = ImagePickerSavePath(
                Environment.getExternalStorageState(),
                isRelative = true
            )
            selectedImages = imageGallerySelected  // original selected images, used in multi mode
        }
        imagePickerLauncher.launch(config)
    }

    private fun onUpdateImageSelected(imageMediaItem: MediaItem.ImageMediaItem) {
        viewModel.mediaImageSelected.value?.find {
            it.uri == imageMediaItem.uri
        }?.let {
            viewModel.input.removeMediaItem(imageMediaItem)
            return
        }
        if (viewModel.mediaImageSelected.value?.size ?: 0 >= LIMIT_IMAGE_SELECTED) {
            displayErrorMessage("Is maximum selected Image")
            return
        }

        viewModel.input.updateSelectedImage(imageMediaItem)
    }

    private fun addImagePerivew(imageMediaItem: List<MediaItem>) {
        enableButtonCast(imageMediaItem.isNotEmpty())
        val fbLayoutManager = FlexboxLayoutManager(
            context
        )
        fbLayoutManager.flexWrap = FlexWrap.WRAP
        fbLayoutManager.flexDirection = FlexDirection.ROW
        fbLayoutManager.justifyContent = JustifyContent.SPACE_AROUND
        fbLayoutManager.justifyContent = JustifyContent.SPACE_BETWEEN
        fbLayoutManager.alignItems = AlignItems.FLEX_START

        with(binding.rvImage) {
            layoutManager = fbLayoutManager
            adapter = ImageFloxBoxAdapter().also {
                imageFloxBoxAdapter = it
            }
        }
        imageFloxBoxAdapter.items = imageMediaItem
        imageFloxBoxAdapter.itemClick.subscribe {
            onImageClick(it)
        }.addToDisposables()
    }

    private fun requestStoragePermission(action: () -> Unit) {
        rxPermissions.permissions(
            listOf(CAMERA, READ_EXTERNAL_STORAGE)
        ).onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                "Core fundamental are based on these permissions",
                "OK",
                "Cancel"
            )
        }.request { allGranted, _, deniedList ->
            if (allGranted) {
                action.invoke()
            } else {
                Toast.makeText(
                    requireContext(),
                    "These permissions are denied: $deniedList",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private lateinit var mentionAdapter: MentionArrayAdapter
    private var mentionData = listOf<MentionUiModel>()

    private fun onBindUserMention() {
        mentionAdapter = MentionArrayAdapter(requireContext(), mentionData)
        with(binding.etInputMessage) {
            setMentionAdapter(mentionAdapter)
            addTextChangedListener { it ->
                viewModel.input.validateMessage(it.toString())
                    .subscribeBy(
                        onError = ::handleOnError
                    ).addToDisposables()
                setMentionTextChangedListener(
                    object : MentionView.OnChangedListener {
                        override fun onChanged(view: MentionView, text: CharSequence) {
                            fetchMentionUser(text)
                        }
                    }
                )
            }
        }
    }

    private fun fetchMentionUser(text: CharSequence) {
        viewModel.getUserMention(text.toString()).subscribeBy(
            onSuccess = {
                onBindMentionItem(it)
            },
            onError = {
                displayError(it)
            }
        ).addToDisposables()
    }

    private fun onBindMentionItem(list: List<MentionUiModel>) {
        mentionData = list
        mentionAdapter.items = list
        mentionAdapter.notifyDataSetChanged()
    }

    private fun handleOnError(case: Throwable) {
        displayError(case)
    }

    private fun handleOnResponse() {
        onBackToHomeFeed()
    }

    private fun enableButtonCast(enable: Boolean) {
        with(binding.btCast) {
            isActivated = enable
            isEnabled = enable
        }
    }
}
