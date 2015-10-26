package com.kickstarter.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;
import android.widget.TextView;

import com.kickstarter.KSApplication;
import com.kickstarter.R;
import com.kickstarter.libs.BaseActivity;
import com.kickstarter.libs.CircleTransform;
import com.kickstarter.libs.Money;
import com.kickstarter.libs.qualifiers.RequiresPresenter;
import com.kickstarter.models.Backing;
import com.kickstarter.models.Project;
import com.kickstarter.presenters.ViewPledgePresenter;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

@RequiresPresenter(ViewPledgePresenter.class)
public class ViewPledgeActivity extends BaseActivity<ViewPledgePresenter> {
  public @Bind(R.id.avatar) ImageView avatarImageView;
  public @Bind(R.id.name) TextView nameTextView;
  public @Bind(R.id.sequence) TextView sequenceTextView;
  public @Bind(R.id.pledge_info) TextView pledgeInfoTextView;
  public @Bind(R.id.pledge_status) TextView pledgeStatusTextView;
  public @Bind(R.id.reward_info) TextView rewardInfoTextView;
  public @Bind(R.id.shipping_info) TextView shippingInfoTextView;
  public @Bind(R.id.shipping_amount) TextView shippingAmountTextView;

  @Inject Money money;

  public void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.view_pledge_layout);
    ButterKnife.bind(this);
    ((KSApplication) getApplication()).component().inject(this);

    final Intent intent = getIntent();
    final Project project = intent.getParcelableExtra(getString(R.string.intent_project));
    presenter.initialize(project);
  }

  public void show(@NonNull final Backing backing) {
    Picasso.with(this).load(backing.backer().avatar().medium())
      .transform(new CircleTransform())
      .into(avatarImageView);
    nameTextView.setText(backing.backer().name());
    sequenceTextView.setText(getString(R.string.Backer_number, backing.formattedSequence()));
    pledgeInfoTextView.setText(String.format(
      getString(R.string.pledged_amount_on_date),
      money.formattedCurrency(backing.amount(), backing.project().currencyOptions()),
      backing.formattedPledgedAt()
    ));
    pledgeStatusTextView.setText(String.format(
      getString(R.string.Status_),
      backing.status()
    ));
    rewardInfoTextView.setText(String.format(
      getString(R.string.reward_amount_description),
      money.formattedCurrency(backing.reward().minimum(), backing.project().currencyOptions()),
      backing.reward().reward()));
    if (backing.reward().shippingEnabled() != null && backing.reward().shippingEnabled()) {
      shippingInfoTextView.setText(backing.location().displayableName());
      shippingAmountTextView.setText(
        money.formattedCurrency(backing.shippingAmount(), backing.project().currencyOptions())
      );
    }
  }

  @OnClick(R.id.nav_back_button)
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    overridePendingTransition(R.anim.fade_in_slide_in_left, R.anim.slide_out_right);
  }
}