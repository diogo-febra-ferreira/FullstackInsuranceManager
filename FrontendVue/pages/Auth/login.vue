<template>
  <div class="d-flex align-items-center justify-content-center">
    <b-container>
      <h3 class="text-center mb-4" style="margin-top: 100px;">
        Occurrences Management
      </h3>
      <b-form @submit.prevent="onSubmit" @reset="onReset" class="mx-auto" style="max-width: 300px">
        <b-form-group label="Username" description="Enter your username">
          <b-input name="username" v-model.trim="username" required placeholder="Your username" />
        </b-form-group>
        <b-form-group label="Password" description="Enter your password">
          <b-input name="password" v-model="password" required type="password" placeholder="Your password" />
        </b-form-group>
        <b-button type="submit" variant="success" block>
          Login
        </b-button>
        <b-button v-on:click="register" variant="success" block>
          Register as client
        </b-button>
      </b-form>
    </b-container>
  </div>
</template>
<script>
export default {
  auth: false, // public page, don’t use the authentication middleware
  data() {
    return {
      username: null,
      password: null
    }
  },
  methods: {
    onSubmit() {
      let promise = this.$auth.loginWith('local', {
        data: {
          username: this.username,
          password: this.password
        }
      })
      promise.then(() => {
        this.$toast.success('You are logged in!').goAway(3000)
        console.log(this.$auth.user)
        this.$router.push(`/${this.$auth.user.role.toLowerCase()}s/` + this.username)
      })
      promise.catch(() => {
        this.$toast.error('Sorry, you cant login. Ensure your credentials are correct').goAway(3000)
      })
    },
    register(){
      this.$router.push('registerClient')
    },
    onReset() {
      this.username = null
      this.password = null
    }
  }
}
</script>

<style scoped>

</style>
