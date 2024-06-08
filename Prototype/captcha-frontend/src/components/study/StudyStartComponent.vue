<template>
  <div class="StudyStartComponent">
    <p> In this study a new type of "Are you human?" detection program will be tested. Your main task will be to
      rate how similar in meaning two words in different sentences are sentences. </p>
<!--   <p> For further information about Captchas see here: <a href="https://en.wikipedia.org/wiki/CAPTCHA" target="_blank">CAPTCHA - Wikipedia</a></p>-->
    <p> The study will take approximately 10 to 15 minutes to complete. Please do not take any breaks while solving the
      challenges.</p>
    <p> There will be <strong> no </strong> personal data collected, the participant answers and metrics ( such as
      mother tongue and taken time ) will be stored anonymously. </p>
    <p>Notice: Try not to reload the page or move backward a page or you have to restart the Study.</p>
    <p> If you are ready press the button below. </p>
<button id="startStudyButton" @click="startStudy" v-if="!buttonPressed"> Start Study</button>
    <p v-else>Waiting to receive study...</p>  </div>
</template>

<script>
export default {
  name: "StudyStartComponent",
  data() {
    return {
      studyStarted: false,
      buttonPressed: false
    };
  },
  methods: {
    startStudy() {
      this.studyStarted = true;
      this.buttonPressed = true;
      this.$emit("studyStarted", true);
    }
  }
};

</script>

<style scoped>

</style>