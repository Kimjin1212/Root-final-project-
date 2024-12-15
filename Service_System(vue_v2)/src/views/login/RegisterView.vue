<template>
  <div class="register-container">
    <img src="../../assets/logo.jpg" style="height: 150px; width: 300px;" />

    <h2>Register</h2>
    <el-form >
      <el-form-item label="Username">
        <el-input v-model="username" />
      </el-form-item>
      <el-form-item label="password">
        <el-input v-model="password" />
      </el-form-item>
      <el-form-item label="Email">
        <el-input v-model="email" />
      </el-form-item>


      <p v-if="successMessage" class="success">{{ successMessage }}</p>
      <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      <el-form-item>
        <a href="/login">To Login</a>
        <el-button type="primary" @click="handleRegister">Register</el-button>
      </el-form-item>
    </el-form> 
  </div>
</template>

<script>
import axios from "axios";

export default {
  data() {
    return {
      username: "",
      password: "",
      email: "",
      successMessage: "",
      errorMessage: "",
    };
  },
  methods: {
    async handleRegister() {
      try {
        const response = await axios.post("http://127.0.0.1:8000/users/register", {
          username: this.username,
          password: this.password,
          email: this.email,
        });
        this.successMessage = "Registration successful! You can now login.";
        this.errorMessage = "";
      } catch (error) {
        this.successMessage = "";
        this.errorMessage = "Registration failed. Please try again.";
      }
    },
  },
};
</script>

<style scoped>
.register-container {
  max-width: 400px;
  margin: auto;
  padding: 20px;
  border: 1px solid #ccc;
}

.success {
  color: green;
}

.error {
  color: red;
}
</style>