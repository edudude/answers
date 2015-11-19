/***************************************************************************
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 ***************************************************************************/
package org.exoplatform.answer.webui.popup;

import org.exoplatform.answer.webui.FAQUtils;
import org.exoplatform.container.PortalContainer;
import org.exoplatform.forum.common.user.CommonContact;
import org.exoplatform.forum.common.user.ContactProvider;
import org.exoplatform.forum.common.webui.UIPopupAction;
import org.exoplatform.services.organization.User;
import org.exoplatform.services.user.UserStateService;
import org.exoplatform.social.core.identity.model.Profile;
import org.exoplatform.social.core.identity.provider.OrganizationIdentityProvider;
import org.exoplatform.social.core.manager.IdentityManager;
import org.exoplatform.webui.config.annotation.ComponentConfig;
import org.exoplatform.webui.config.annotation.EventConfig;
import org.exoplatform.webui.core.UIPopupComponent;
import org.exoplatform.webui.core.lifecycle.UIFormLifecycle;
import org.exoplatform.webui.event.Event;
import org.exoplatform.webui.event.EventListener;
import org.exoplatform.webui.form.UIForm;

@ComponentConfig(
    lifecycle = UIFormLifecycle.class, 
    template = "app:/templates/answer/webui/popup/UIViewUserProfile.gtmpl", 
    events = {
        @EventConfig(listeners = UIViewUserProfile.CloseActionListener.class) 
    }
)
public class UIViewUserProfile extends UIForm implements UIPopupComponent {
  public User user_;

  public UIViewUserProfile() throws Exception {
  }

  protected String getAvatarUrl(String userId) throws Exception {
    return FAQUtils.getUserAvatar(userId);
  }

  public void setUser(User userName) {
    this.user_ = userName;
  }

  public User getUser() throws Exception {
    return user_;
  }

  protected String[] getLabelProfile() throws Exception {
    return new String[] { getLabel("userName"), getLabel("firstName"), getLabel("lastName"), getLabel("birthDay"), getLabel("gender"), 
        getLabel("email"), getLabel("jobTitle"), getLabel("location"), getLabel("homePhone"), getLabel("workPhone"), getLabel("website")};
  }

  public CommonContact getContact(String userId) {
    try {
      ContactProvider provider = (ContactProvider) PortalContainer.getComponent(ContactProvider.class);
      return provider.getCommonContact(userId);
    } catch (Exception e) {
      return new CommonContact();
    }
  }

  public String getUserProfileURL(String username) {
    IdentityManager identityManager = getApplicationComponent(IdentityManager.class);
    Profile profile = identityManager.getOrCreateIdentity(OrganizationIdentityProvider.NAME, username, false).getProfile();
    return profile.getUrl();
  }

  public boolean isOnline(String userId) {
    UserStateService userStateService = getApplicationComponent(UserStateService.class);
    return userStateService.isOnline(userId);
  }

  public void activate() {
  }

  public void deActivate() {
  }

  static public class CloseActionListener extends EventListener<UIViewUserProfile> {
    public void execute(Event<UIViewUserProfile> event) throws Exception {
      UIViewUserProfile uiViewUserProfile = event.getSource();
      UIPopupAction uiPopupAction = uiViewUserProfile.getAncestorOfType(UIPopupAction.class);
      uiPopupAction.deActivate();
      event.getRequestContext().addUIComponentToUpdateByAjax(uiPopupAction);
    }
  }
}
