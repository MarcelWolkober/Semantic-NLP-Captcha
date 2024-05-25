<script>
export default {
  name: "CaptchaFailed"
};
</script>

<template>
  <div class="captchaFailed">
    <h1>CaptchaFailed</h1>
    <p>Failed to solve the captcha.</p>
    <router-link :to="{name:'home'}">Go Home</router-link>
  </div>
</template>

<style scoped>

</style>