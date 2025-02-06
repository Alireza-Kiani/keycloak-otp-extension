<#import "template.ftl" as layout>
<@layout.registrationLayout displayInfo=social.displayInfo; section>
    <#if section = "title">
        ${msg("loginTitle",(realm.displayName!''))}
    <#elseif section = "header">
        ${msg("loginTitleHtml",(realm.displayNameHtml!''))?no_esc}
    <#elseif section = "form">
            <form id="kc-form-login" class="${properties.kcFormClass!}" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                <div class="${properties.kcFormGroupClass!}">
                    <div class="${properties.kcLabelWrapperClass!}">
                        <label for="phone-number" class="${properties.kcLabelClass!}">${msg("enterPhoneNumber")}</label>
                    </div>

                    <div class="${properties.kcInputWrapperClass!}">
                        <input tabindex="1" id="phone-number" class="${properties.kcInputClass!}" name="phone-number"  type="text" autofocus autocomplete="on" />
                    </div>
                </div>

                <div class="${properties.kcFormGroupClass!}">
                    <div id="kc-form-options" class="${properties.kcFormOptionsClass!}">
                    </div>

                    <div id="kc-form-buttons" class="${properties.kcFormButtonsClass!}">
                        <div class="${properties.kcFormButtonsWrapperClass!}">
                            <input tabindex="4" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonLargeClass!}" name="sendCode" id="sendCode" type="submit" value="${msg("sendCode")}"/>
                        </div>
                     </div>
                </div>
            </form>
    </#if>
</@layout.registrationLayout>
