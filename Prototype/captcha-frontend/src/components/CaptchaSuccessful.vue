<script>
export default {
  name: "CaptchaSuccessful"
};
</script>

<template>
  <div class="captchaSuccessful">
    <h1>CaptchaSuccessful</h1>
    <p>Captcha solved successfully.</p>
    <router-link :to="{name:'home'}">Go Home</router-link>
  </div>
</template>

<style scoped>

</style>