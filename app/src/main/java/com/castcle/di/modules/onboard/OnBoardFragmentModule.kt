package com.castcle.di.modules.onboard

import com.castcle.di.modules.blog.*
import com.castcle.di.modules.common.dialog.*
import com.castcle.di.modules.feed.FeedDetailFragmentViewModelModule
import com.castcle.di.modules.feed.FeedFragmentViewModelModule
import com.castcle.di.modules.forgotpass.SearchAccountFragmentViewModelModule
import com.castcle.di.modules.forgotpass.VerifyOtpFragmentViewModelModule
import com.castcle.di.modules.login.*
import com.castcle.di.modules.notification.NotificationFragmentViewModelModule
import com.castcle.di.modules.profile.*
import com.castcle.di.modules.report.ReportFragmentViewModelModule
import com.castcle.di.modules.search.*
import com.castcle.di.modules.setting.*
import com.castcle.di.modules.webview.WebViewFragmentViewModelModule
import com.castcle.di.scope.FragmentScope
import com.castcle.ui.common.dialog.NotiflyLoginDialogFragment
import com.castcle.ui.common.dialog.chooseimage.ChooseDialogFragment
import com.castcle.ui.common.dialog.dialogeditcontent.EditContentDialogFragment
import com.castcle.ui.common.dialog.profilechoose.ProfileChooseDialogFragment
import com.castcle.ui.common.dialog.recast.RecastDialogFragment
import com.castcle.ui.common.dialog.user.UserChooseDialogFragment
import com.castcle.ui.createbloc.CreateBlogFragment
import com.castcle.ui.createbloc.CreateQuoteFragment
import com.castcle.ui.createpost.CreatePostFragment
import com.castcle.ui.feed.FeedFragment
import com.castcle.ui.feed.feeddetail.FeedDetailFragment
import com.castcle.ui.forgotpassword.searchaccount.SearchAccountFragment
import com.castcle.ui.forgotpassword.verifyotp.VerifyOtpFragment
import com.castcle.ui.login.LoginFragment
import com.castcle.ui.login.twitterlogin.TwitterLoginFragment
import com.castcle.ui.profile.CropAvatarImageFragment
import com.castcle.ui.profile.ProfileFragment
import com.castcle.ui.profile.childview.all.ContentAllFragment
import com.castcle.ui.profile.childview.blog.ContentPostFragment
import com.castcle.ui.profile.childview.photo.ContentBlogFragment
import com.castcle.ui.profile.childview.post.ContentPhotoFragment
import com.castcle.ui.report.ReportFragment
import com.castcle.ui.report.ReportFragmentViewModel
import com.castcle.ui.search.TrendSearchFragment
import com.castcle.ui.search.onsearch.SearchFragment
import com.castcle.ui.search.trend.TrendFragment
import com.castcle.ui.search.trend.childview.*
import com.castcle.ui.setting.SettingFragment
import com.castcle.ui.setting.applanguage.AppLanguageFragment
import com.castcle.ui.setting.changepassword.ChangePasswordFragment
import com.castcle.ui.setting.changepassword.complete.CompleteFragment
import com.castcle.ui.setting.changepassword.createnewpassword.CreatePasswordFragment
import com.castcle.ui.setting.deleteaccount.DeletePageFragment
import com.castcle.ui.setting.language.LanguageAppFragment
import com.castcle.ui.setting.notification.NotificationFragment
import com.castcle.ui.setting.notification.page.ProfileNotificationFragment
import com.castcle.ui.setting.notification.profile.PageNotificationFragment
import com.castcle.ui.setting.notification.system.SystemNotificationFragment
import com.castcle.ui.setting.page.GreetingPageFragment
import com.castcle.ui.setting.account.SettingProfileFragment
import com.castcle.ui.signin.aboutyou.AboutYouFragment
import com.castcle.ui.signin.aboutyou.addlink.AddLinksFragment
import com.castcle.ui.signin.createaccount.CreateAccountFragment
import com.castcle.ui.signin.createaccount.accountcomplete.AccountCompleteFragment
import com.castcle.ui.signin.createdisplayname.CreateDisplayNameFragment
import com.castcle.ui.signin.email.EmailFragment
import com.castcle.ui.signin.geetingsignin.GreetingSignInFragment
import com.castcle.ui.signin.password.PasswordFragment
import com.castcle.ui.signin.profilechooseimage.ProfileChooseFragment
import com.castcle.ui.signin.verifyemail.ResentVerifyEmailFragment
import com.castcle.ui.signin.verifyemail.VerifyEmailFragment
import com.castcle.ui.webview.WebViewFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

//  Copyright (c) 2021, Castcle and/or its affiliates. All rights reserved.
//  DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
//
//  This code is free software; you can redistribute it and/or modify it
//  under the terms of the GNU General Public License version 3 only, as
//  published by the Free Software Foundation.
//
//  This code is distributed in the hope that it will be useful, but WITHOUT
//  ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
//  FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
//  version 3 for more details (a copy is included in the LICENSE file that
//  accompanied this code).
//
//  You should have received a copy of the GNU General Public License version
//  3 along with this work; if not, write to the Free Software Foundation,
//  Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
//
//  Please contact Castcle, 22 Phet Kasem 47/2 Alley, Bang Khae, Bangkok,
//  Thailand 10160, or visit www.castcle.com if you need additional information
//  or have any questions.
//
//
//  Created by sklim on 18/8/2021 AD at 14:49.

@Module
interface OnBoardFragmentModule {

    @FragmentScope
    @ContributesAndroidInjector(modules = [FeedFragmentViewModelModule::class])
    fun feedFragment(): FeedFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [NotiflyLoginDialogFragmentModule::class])
    fun notiflyLoginDialogFragment(): NotiflyLoginDialogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [RecastDialogFragmentModule::class])
    fun recastDialogFragment(): RecastDialogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [WebViewFragmentViewModelModule::class])
    fun webViewFragment(): WebViewFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [LoginFragmentViewModelModule::class])
    fun loginFragment(): LoginFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [EmailFragmentViewModelModule::class])
    fun emailFragment(): EmailFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [PasswordFragmentViewModelModule::class])
    fun passwordFragment(): PasswordFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [GreetingFragmentViewModelModule::class])
    fun greetingSignInFragment(): GreetingSignInFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CreateDisplayNameViewModelModule::class])
    fun createDisplayNameFragment(): CreateDisplayNameFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileChooseViewModelModule::class])
    fun profileChooseFragment(): ProfileChooseFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AboutYouViewModelModule::class])
    fun aboutYouFragment(): AboutYouFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [VerifyEmailViewModelModule::class])
    fun verifyEmailFragment(): VerifyEmailFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ResentVerifyEmailViewModelModule::class])
    fun resentVerifyEmailFragment(): ResentVerifyEmailFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [SettingFragmentViewModelModule::class])
    fun settingFragment(): SettingFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentViewModelModule::class])
    fun profileFragment(): ProfileFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentViewModelModule::class])
    fun contentAllFragment(): ContentAllFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentViewModelModule::class])
    fun contentPostFragment(): ContentPostFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentViewModelModule::class])
    fun contentBlogFragment(): ContentBlogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileFragmentViewModelModule::class])
    fun contentPhotoFragment(): ContentPhotoFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CreateBlogFragmentViewModelModule::class])
    fun createBlogFragment(): CreateBlogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CreateQuoteFragmentViewModelModule::class])
    fun createQuoteFragment(): CreateQuoteFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TrendSearchFragmentViewModelModule::class])
    fun trendSearchFragment(): TrendSearchFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [LanguageFragmentViewModelModule::class])
    fun languageFragment(): LanguageAppFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [LanguageFragmentViewModelModule::class])
    fun appLanguageFragment(): AppLanguageFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [SettingProfileFragmentViewModelModule::class])
    fun settingProfileFragment(): SettingProfileFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ChangePasswordFragmentViewModelModule::class])
    fun changePasswordFragment(): ChangePasswordFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CreatePasswordFragmentViewModelModule::class])
    fun createPasswordFragment(): CreatePasswordFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CompleteFragmentViewModelModule::class])
    fun completeFragment(): CompleteFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [FeedDetailFragmentViewModelModule::class])
    fun feedDetailFragment(): FeedDetailFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [SearchFragmentViewModelModule::class])
    fun searchFragment(): SearchFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TrendFragmentViewModelModule::class])
    fun trendFragment(): TrendFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TrendFragmentViewModelModule::class])
    fun topFragment(): TopFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TrendFragmentViewModelModule::class])
    fun peopleFragment(): PeopleFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TrendFragmentViewModelModule::class])
    fun photoFragment(): PhotoFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TrendFragmentViewModelModule::class])
    fun lastestFragment(): LastestFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TrendFragmentViewModelModule::class])
    fun videoFragment(): VideoFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [GreetingPageFragmentViewModelModule::class])
    fun greeingPageFragment(): GreetingPageFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AddLinksFragmentViewModelModule::class])
    fun addLinksFragment(): AddLinksFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ChooseDialogFragmentModule::class])
    fun chooseDialogFragment(): ChooseDialogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CropAvatarImageViewModelModule::class])
    fun cropAvatarImageFragment(): CropAvatarImageFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [DeleteFragmentViewModelModule::class])
    fun deletePageFragment(): DeletePageFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileChooseDialogViewModelModule::class])
    fun profileChooseDialogFragment(): ProfileChooseDialogFragment


    @FragmentScope
    @ContributesAndroidInjector(modules = [ProfileChooseDialogViewModelModule::class])
    fun userChooseDialogFragment(): UserChooseDialogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [NotificationFragmentViewModelModule::class])
    fun notificationFragment(): NotificationFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [NotificationFragmentViewModelModule::class])
    fun profileNotificationFragment(): ProfileNotificationFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [NotificationFragmentViewModelModule::class])
    fun pageNotificationFragment(): PageNotificationFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [NotificationFragmentViewModelModule::class])
    fun systemnotificationFragment(): SystemNotificationFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [SearchAccountFragmentViewModelModule::class])
    fun searchAccountFragment(): SearchAccountFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [VerifyOtpFragmentViewModelModule::class])
    fun verifyOtpFragment(): VerifyOtpFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TwitterLoginViewModelModule::class])
    fun twitterLoginFragment(): TwitterLoginFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CreateAccountViewModelModule::class])
    fun createAccountFragment(): CreateAccountFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [AccountCompleteViewModelModule::class])
    fun accountCompleteFragment(): AccountCompleteFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [CreatePostFragmentViewModelModule::class])
    fun createPostFragment(): CreatePostFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [EditContentDialogFragmentModule::class])
    fun editContentDialogFragment(): EditContentDialogFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [ReportFragmentViewModelModule::class])
    fun reportFragment(): ReportFragment
}
